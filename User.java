import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * ABSTRACTION: User is an abstract class. It holds what every user has in common
 * (id, name, password) and forces each subclass to define its own menu.
 */
public abstract class User {

    // ENCAPSULATION: fields are private, accessed only through getters
    private String userId;
    private String name;
    private String passwordHash;   // we never store the plain password

    // CONSTRUCTOR
    public User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.passwordHash = hash(password);
    }

    public String getUserId() { return userId; }
    public String getName()   { return name; }

    public void setName(String name) { this.name = name; }

    /** Returns true if the typed password matches the stored one. */
    public boolean checkPassword(String password) {
        return passwordHash.equals(hash(password));
    }

    /** POLYMORPHISM: every subclass overrides this with its own menu. */
    public abstract void displayMenu();

    // Simple SHA-256 hashing so the password is not kept as plain text
    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return text; // fallback, should never happen
        }
    }
}
