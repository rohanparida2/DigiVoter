import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Web version of DigiVoter. Starts a tiny HTTP server on http://localhost:8080
 * It reuses the SAME OOP classes (User, Voter, Admin, Candidate, Election).
 * The browser page (web/index.html) talks to it using simple JSON requests.
 */
public class WebMain {

    private static Election election = new Election();
    private static Map<String, User> sessions = new HashMap<>();   // token -> logged-in user

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", WebMain::handle);
        server.start();
        System.out.println("DigiVoter is running. Open http://localhost:8080 in your browser.");
        System.out.println("Press Ctrl+C to stop.");
    }

    private static void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/favicon.ico")) { ex.sendResponseHeaders(204, -1); ex.close(); return; }
        String type = "application/json; charset=utf-8";
        byte[] out;
        int code = 200;
        try {
            if (path.startsWith("/api/")) {
                String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                out = api(path.substring(5), body).getBytes(StandardCharsets.UTF_8);
            } else {
                type = "text/html; charset=utf-8";
                out = Files.readAllBytes(findPage());
            }
        } catch (Exception e) {
            code = 500;
            out = fail("Server error: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        }
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, out.length);
        ex.getResponseBody().write(out);
        ex.close();
    }

    private static Path findPage() throws IOException {
        for (String p : new String[]{"web/index.html", "../web/index.html"})
            if (Files.exists(Path.of(p))) return Path.of(p);
        throw new IOException("web/index.html not found. Keep the web folder next to the src folder.");
    }

    // ---------- API ----------

    private static String api(String route, String body) {
        String token = field(body, "token");
        User user = sessions.get(token);
        switch (route) {
            case "register": {
                try {
                    int age = Integer.parseInt(field(body, "age").trim());
                    String id = field(body, "id").trim(), name = field(body, "name").trim(), pw = field(body, "password");
                    if (id.isEmpty() || name.isEmpty() || pw.isEmpty()) return fail("All fields are required.");
                    election.registerVoter(id, name, age, pw);      // overloaded method, version 1
                    return ok("Registration successful! Please log in.", null);
                } catch (NumberFormatException e) {
                    return fail("Age must be a number.");
                } catch (IllegalArgumentException e) {
                    return fail(e.getMessage());
                }
            }
            case "login": {
                Voter v = election.loginVoter(field(body, "id").trim(), field(body, "password"));
                if (v == null) return fail("Invalid credentials.");
                return ok("Welcome", session(v, "voter"));
            }
            case "admin-login": {
                Admin a = election.loginAdmin(field(body, "id").trim(), field(body, "password"));
                if (a == null) return fail("Invalid credentials.");
                return ok("Welcome", session(a, "admin"));
            }
            case "candidates":
                return ok("", candidatesJson(false));
            case "status": {
                if (!(user instanceof Voter)) return fail("Please log in again.");
                Voter v = (Voter) user;
                return ok("", "{\"name\":" + q(v.getName()) + ",\"hasVoted\":" + v.hasVoted()
                        + ",\"totalVotes\":" + election.getVotesCast() + "}");
            }
            case "vote": {
                if (!(user instanceof Voter)) return fail("Please log in again.");
                try {
                    election.castVote((Voter) user, Integer.parseInt(field(body, "candidateId").trim()));
                    return ok("Your vote has been recorded.", "{\"confirmation\":\"DV-"
                            + (100000 + new Random().nextInt(900000)) + "\",\"totalVotes\":" + election.getVotesCast() + "}");
                } catch (NumberFormatException e) {
                    return fail("Please select a candidate.");
                } catch (RuntimeException e) {
                    return fail(e.getMessage());
                }
            }
            case "add-candidate": {
                if (!(user instanceof Admin)) return fail("Admin access only.");
                String name = field(body, "name").trim(), party = field(body, "party").trim();
                if (name.isEmpty() || party.isEmpty()) return fail("Name and party are required.");
                election.addCandidate(name, party);
                return ok("Candidate added.", null);
            }
            case "results": {
                if (!(user instanceof Admin)) return fail("Admin access only.");
                int reg = election.getTotalVoters(), cast = election.getVotesCast();
                double turnout = reg == 0 ? 0 : cast * 100.0 / reg;
                return ok("", "{\"candidates\":" + candidatesJson(true) + ",\"winner\":" + q(election.getWinnerSummary())
                        + ",\"registered\":" + reg + ",\"votesCast\":" + cast
                        + ",\"turnout\":" + String.format(Locale.US, "%.1f", turnout) + "}");
            }
            case "logout":
                sessions.remove(token);
                return ok("Logged out.", null);
            default:
                return fail("Unknown request.");
        }
    }

    private static String session(User u, String role) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, u);
        return "{\"token\":" + q(token) + ",\"role\":" + q(role) + ",\"name\":" + q(u.getName()) + "}";
    }

    private static String candidatesJson(boolean withVotes) {
        StringBuilder sb = new StringBuilder("[");
        for (Candidate c : election.getCandidates()) {
            if (sb.length() > 1) sb.append(",");
            sb.append("{\"id\":").append(c.getCandidateId()).append(",\"name\":").append(q(c.getName()))
              .append(",\"party\":").append(q(c.getParty()));
            if (withVotes) sb.append(",\"votes\":").append(c.getVoteCount());
            sb.append("}");
        }
        return sb.append("]").toString();
    }

    // ---------- tiny JSON helpers (no libraries needed) ----------

    private static String q(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
    private static String ok(String msg, String data) {
        return "{\"success\":true,\"message\":" + q(msg) + (data == null ? "" : ",\"data\":" + data) + "}";
    }
    private static String fail(String msg) {
        return "{\"success\":false,\"message\":" + q(msg) + "}";
    }
    /** Reads one value from a flat JSON object like {"id":"v1","age":20}. */
    private static String field(String body, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(\"((?:[^\"\\\\]|\\\\.)*)\"|-?\\d+)").matcher(body);
        if (!m.find()) return "";
        return m.group(2) != null ? m.group(2).replace("\\\"", "\"").replace("\\\\", "\\") : m.group(1);
    }
}
