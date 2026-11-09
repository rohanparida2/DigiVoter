/** INHERITANCE: Admin IS-A User. */
public class Admin extends User {

    public Admin(String adminId, String name, String password) {
        super(adminId, name, password);
    }

    /** POLYMORPHISM: Admin's own version of displayMenu(). */
    @Override
    public void displayMenu() {
        System.out.println("\n================================");
        System.out.println("         ADMIN DASHBOARD");
        System.out.println("================================");
        System.out.println("1. Add Candidate");
        System.out.println("2. View Candidates");
        System.out.println("3. View Election Results");
        System.out.println("4. View Voting Statistics");
        System.out.println("5. Logout");
    }
}
