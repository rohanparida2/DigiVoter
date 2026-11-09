import java.util.Scanner;

/** Entry point: console menus for DigiVoter. */
public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static Election election = new Election();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n================================");
            System.out.println("          DIGIVOTER");
            System.out.println("   Digital Voting Management");
            System.out.println("================================");
            System.out.println("1. Voter Registration");
            System.out.println("2. Voter Login");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");

            switch (readInt("\nEnter your choice: ")) {
                case 1: registerVoter(); break;
                case 2: voterLogin();    break;
                case 3: adminLogin();    break;
                case 4: running = false; System.out.println("Thank you for using DigiVoter. Goodbye!"); break;
                default: System.out.println("Invalid choice. Please enter 1-4.");
            }
        }
    }

    // ---------- Input helpers (exception handling) ----------

    /** Keeps asking until the user types a valid whole number. */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    /** Keeps asking until the user types something that is not empty. */
    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("This field cannot be empty.");
        }
    }

    // ---------- Voter flow ----------

    private static void registerVoter() {
        System.out.println("\n--- Voter Registration ---");
        String id = readText("Voter ID: ");
        String name = readText("Name: ");
        int age = readInt("Age: ");
        String pass = readText("Password: ");
        try {
            election.registerVoter(id, name, age, pass);
            System.out.println("Registration successful! You can now log in.");
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void voterLogin() {
        System.out.println("\n--- Voter Login ---");
        String id = readText("Voter ID: ");
        String pass = readText("Password: ");
        Voter voter = election.loginVoter(id, pass);
        if (voter == null) {
            System.out.println("Invalid credentials.");
            return;
        }
        boolean loggedIn = true;
        while (loggedIn) {
            voter.displayMenu();    // POLYMORPHISM in action
            switch (readInt("\nEnter your choice: ")) {
                case 1: showCandidates(); break;
                case 2: castVote(voter);  break;
                case 3:
                    System.out.println(voter.hasVoted()
                        ? "Status: You have already voted. Thank you!"
                        : "Status: You have NOT voted yet.");
                    break;
                case 4: loggedIn = false; System.out.println("Logged out."); break;
                default: System.out.println("Invalid choice. Please enter 1-4.");
            }
        }
    }

    private static void showCandidates() {
        if (election.getCandidates().isEmpty()) {
            System.out.println("No candidates available yet.");
            return;
        }
        System.out.println("\n--- Candidates ---");
        for (Candidate c : election.getCandidates()) System.out.println(c);
    }

    private static void castVote(Voter voter) {
        if (election.getCandidates().isEmpty()) {
            System.out.println("No candidates available yet.");
            return;
        }
        if (voter.hasVoted()) {
            System.out.println("You have already voted. One vote per voter!");
            return;
        }
        showCandidates();
        int choice = readInt("Enter candidate number: ");
        try {
            election.castVote(voter, choice);
            System.out.println("Your vote has been recorded. Thank you for voting!");
        } catch (RuntimeException e) {
            System.out.println("Vote failed: " + e.getMessage());
        }
    }

    // ---------- Admin flow ----------

    private static void adminLogin() {
        System.out.println("\n--- Admin Login ---");
        String id = readText("Admin ID: ");
        String pass = readText("Password: ");
        Admin admin = election.loginAdmin(id, pass);
        if (admin == null) {
            System.out.println("Invalid credentials.");
            return;
        }
        boolean loggedIn = true;
        while (loggedIn) {
            admin.displayMenu();    // POLYMORPHISM in action
            switch (readInt("\nEnter your choice: ")) {
                case 1:
                    String name = readText("Candidate name: ");
                    String party = readText("Party: ");
                    election.addCandidate(name, party);
                    System.out.println("Candidate added.");
                    break;
                case 2: showCandidates();            break;
                case 3: election.showResults();      break;
                case 4: election.showStatistics();   break;
                case 5: loggedIn = false; System.out.println("Logged out."); break;
                default: System.out.println("Invalid choice. Please enter 1-5.");
            }
        }
    }
}
