import java.util.ArrayList;

/**
 * Election holds all voters and candidates (in ArrayLists) and
 * contains the business logic: registration, login, voting, results.
 */
public class Election {

    private ArrayList<Voter> voters = new ArrayList<>();
    private ArrayList<Candidate> candidates = new ArrayList<>();
    private Admin admin = new Admin("admin", "Administrator", "admin123");
    private int nextCandidateId = 1;

    /** Loads fictional demo candidates. */
    public Election() {
        addCandidate("Aarav Sharma", "Unity Party");
        addCandidate("Meera Iyer", "Progress Alliance");
        addCandidate("Kabir Singh", "People's Front");
    }

    // ---------- Registration (METHOD OVERLOADING: two versions) ----------

    /** Version 1: register using raw details. */
    public void registerVoter(String id, String name, int age, String password) {
        registerVoter(new Voter(id, name, age, password));
    }

    /** Version 2: register an already created Voter object. */
    public void registerVoter(Voter v) {
        if (v.getAge() < 18)
            throw new IllegalArgumentException("You must be 18 or older to register.");
        if (findVoter(v.getUserId()) != null)
            throw new IllegalArgumentException("This Voter ID is already registered.");
        voters.add(v);
    }

    // ---------- Login ----------

    /** Returns the Voter if the credentials are right, otherwise null. */
    public Voter loginVoter(String id, String password) {
        Voter v = findVoter(id);
        if (v != null && v.checkPassword(password)) return v;
        return null;
    }

    public Admin loginAdmin(String id, String password) {
        if (admin.getUserId().equals(id) && admin.checkPassword(password)) return admin;
        return null;
    }

    private Voter findVoter(String id) {
        for (Voter v : voters)
            if (v.getUserId().equalsIgnoreCase(id)) return v;
        return null;
    }

    // ---------- Candidates ----------

    public void addCandidate(String name, String party) {
        candidates.add(new Candidate(nextCandidateId++, name, party));
    }

    public ArrayList<Candidate> getCandidates() { return candidates; }

    // ---------- Voting ----------

    /** Casts a vote. Throws an exception with a clear message if not allowed. */
    public void castVote(Voter voter, int candidateId) {
        if (candidates.isEmpty())
            throw new IllegalStateException("There are no candidates yet.");
        if (voter.hasVoted())
            throw new IllegalStateException("You have already voted. One vote per voter!");
        Candidate chosen = null;
        for (Candidate c : candidates)
            if (c.getCandidateId() == candidateId) chosen = c;
        if (chosen == null)
            throw new IllegalArgumentException("Invalid candidate number.");
        chosen.addVote();
        voter.setHasVoted(true);
    }

    // ---------- Results and statistics ----------

    public int getTotalVoters() { return voters.size(); }

    public int getVotesCast() {
        int total = 0;
        for (Candidate c : candidates) total += c.getVoteCount();
        return total;
    }

    /** Prints vote counts and the winner (or a tie). */
    public void showResults() {
        if (candidates.isEmpty()) { System.out.println("No candidates yet."); return; }
        System.out.println("\n----- ELECTION RESULTS -----");
        int max = 0;
        for (Candidate c : candidates) {
            System.out.println(c.getName() + " (" + c.getParty() + ") : " + c.getVoteCount() + (c.getVoteCount() == 1 ? " vote" : " votes"));
            if (c.getVoteCount() > max) max = c.getVoteCount();
        }
        if (max == 0) { System.out.println("\nNo votes have been cast yet."); return; }

        ArrayList<Candidate> top = new ArrayList<>();
        for (Candidate c : candidates)
            if (c.getVoteCount() == max) top.add(c);

        if (top.size() == 1) {
            System.out.println("\nWINNER: " + top.get(0).getName() + " (" + top.get(0).getParty() + ")");
        } else {
            System.out.print("\nIT'S A TIE between: ");
            for (int i = 0; i < top.size(); i++)
                System.out.print(top.get(i).getName() + (i < top.size() - 1 ? ", " : ""));
            System.out.println();
        }
    }

    /** One-line result summary (used by the web page). */
    public String getWinnerSummary() {
        int max = 0;
        for (Candidate c : candidates) if (c.getVoteCount() > max) max = c.getVoteCount();
        if (max == 0) return "No votes have been cast yet.";
        ArrayList<String> top = new ArrayList<>();
        for (Candidate c : candidates)
            if (c.getVoteCount() == max) top.add(c.getName() + " (" + c.getParty() + ")");
        return top.size() == 1 ? "WINNER: " + top.get(0) : "TIE: " + String.join(", ", top);
    }

    public void showStatistics() {
        int registered = getTotalVoters();
        int cast = getVotesCast();
        System.out.println("\n----- VOTING STATISTICS -----");
        System.out.println("Registered voters : " + registered);
        System.out.println("Votes cast        : " + cast);
        double turnout = registered == 0 ? 0 : (cast * 100.0 / registered);
        System.out.printf("Turnout           : %.1f%%%n", turnout);
    }
}
