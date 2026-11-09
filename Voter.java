/** INHERITANCE: Voter IS-A User. Adds age and the "has voted" flag. */
public class Voter extends User {

    private int age;
    private boolean hasVoted;

    public Voter(String voterId, String name, int age, String password) {
        super(voterId, name, password);   // call the parent constructor
        this.age = age;
        this.hasVoted = false;
    }

    public int getAge() { return age; }
    public boolean hasVoted() { return hasVoted; }
    public void setHasVoted(boolean hasVoted) { this.hasVoted = hasVoted; }

    /** POLYMORPHISM: Voter's own version of displayMenu(). */
    @Override
    public void displayMenu() {
        System.out.println("\n================================");
        System.out.println("         VOTER DASHBOARD");
        System.out.println("================================");
        System.out.println("Welcome, " + getName() + "!\n");
        System.out.println("1. View Candidates");
        System.out.println("2. Cast Your Vote");
        System.out.println("3. View Voting Status");
        System.out.println("4. Logout");
    }
}
