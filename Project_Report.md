# DigiVoter - Digital Voting Management System
## Object-Oriented Programming (Java) Mini Project Report

---

### Project Details
- **Project Title:** DigiVoter – Digital Voting Management System
- **Subject:** Object-Oriented Programming (OOP) with Java
- **Language & Runtime:** Java (JDK 17 or higher)
- **Architecture:** Object-Oriented Design with Dual Interfaces (CLI Console & Localhost Web Browser)
- **Scope:** Educational Simulation of an Election System

---

## 1. Abstract & Project Overview

**DigiVoter** is an Object-Oriented Java mini project that simulates a modern, secure digital voting system. Physical paper ballot voting systems often suffer from manual counting mistakes, long queues, and difficult tracking. DigiVoter provides a clean, digitized solution where eligible voters can register, view standing candidates, cast their vote exactly once, and check their voting status. At the same time, election officials (Admin) can add new candidates, inspect live vote tallies, detect winners or ties, and view voter turnout percentages.

The core of DigiVoter is built completely using pure Java Object-Oriented principles. To demonstrate the flexibility of modular OOP design, the exact same backend classes (`User`, `Voter`, `Admin`, `Candidate`, and `Election`) power **two separate frontends**:
1. **Console Interface (`Main.java`):** A menu-driven command-line terminal application with robust input validation.
2. **Web Browser Interface (`WebMain.java` + `web/index.html`):** A modern browser-based UI hosted by Java's built-in lightweight HTTP server without needing any heavy third-party web frameworks.

---

## 2. Project Objectives

1. **Demonstrate Core OOP Concepts:** Implement all four main pillars of OOP—**Encapsulation**, **Inheritance**, **Polymorphism**, and **Abstraction**—along with Method Overloading, Method Overriding, and Java Collections.
2. **Ensure Election Integrity:**
   - Enforce legal voting age (minimum 18 years old).
   - Prevent duplicate voter registrations with the same Voter ID.
   - Enforce the **one-person-one-vote** rule so no voter can vote more than once.
3. **Protect User Credentials:** Hash passwords using the cryptographic **SHA-256** hashing algorithm so that passwords are never saved or compared in plain text.
4. **Crash-Proof User Input:** Use structured Java exception handling (`try-catch`) so invalid text or out-of-range choices never crash the application.
5. **Real-Time Result Calculation:** Automatically calculate winner announcements, detect ties, and compute voter turnout statistics.

---

## 3. System Architecture & Class Design

The software follows a clear separation of concerns where business rules, data models, and user interfaces are decoupled.

### Class Hierarchy & System Diagram

```
                       +-------------------------+
                       |       <<abstract>>      |
                       |           User          |
                       +-------------------------+
                       | - userId: String        |
                       | - name: String          |
                       | - passwordHash: String  |
                       +-------------------------+
                       | + displayMenu()*        |
                       | + checkPassword()       |
                       | - hash() [SHA-256]      |
                       +-------------------------+
                                    |
                    +---------------+---------------+
                    |                               |
                    v                               v
     +-----------------------------+ +-----------------------------+
     |            Voter            | |            Admin            |
     +-----------------------------+ +-----------------------------+
     | - age: int                  | |                             |
     | - hasVoted: boolean         | +-----------------------------+
     +-----------------------------+ | + displayMenu() (overridden)|
     | + displayMenu() (overridden)| +-----------------------------+
     | + getAge(), hasVoted()      |
     | + setHasVoted()             |
     +-----------------------------+

     +-----------------------------+ +-----------------------------+
     |          Candidate          | |          Election           |
     +-----------------------------+ +-----------------------------+
     | - candidateId: int          | | - voters: ArrayList<Voter>  |
     | - name: String              | | - candidates: ArrayList<..> |
     | - party: String             | | - admin: Admin              |
     | - voteCount: int            | | - nextCandidateId: int      |
     +-----------------------------+ +-----------------------------+
     | + addVote()                 | | + registerVoter(...) (x2)   |
     | + toString() (overridden)   | | + loginVoter(), loginAdmin()|
     +-----------------------------+ | + castVote()                |
                                     | + showResults()             |
                                     | + getWinnerSummary()        |
                                     | + showStatistics()          |
                                     +-----------------------------+
                                                    ^
                                                    | (used by both)
                     +------------------------------+------------------------------+
                     |                                                             |
      +-----------------------------+                               +-----------------------------+
      |       Main (Console)        |                               |       WebMain (HTTP)        |
      +-----------------------------+                               +-----------------------------+
      | + main(String[] args)       |                               | + main(String[] args)       |
      | - readInt(), readText()     |                               | - HttpServer (port 8080)    |
      | - voterLogin(), adminLogin()|                               | - REST-like /api/ routes    |
      +-----------------------------+                               | - serves web/index.html     |
                                                                    +-----------------------------+
```

---

## 4. Source Code Walkthrough & Class Details

### 4.1. `User.java` (Abstract Base Class)
- **Purpose:** Acts as the generic blueprint for any account holder in DigiVoter.
- **Attributes:**
  - `private String userId`: Unique username/ID.
  - `private String name`: Person's display name.
  - `private String passwordHash`: Secure SHA-256 hash string (plain text is never saved).
- **Core Methods:**
  - `public User(String userId, String name, String password)`: Constructor initializing fields and computing the hash of the password.
  - `public boolean checkPassword(String password)`: Hashes user input and compares it with the stored hash digest.
  - `public abstract void displayMenu()`: Abstract method enforcing that subclasses define their own specific dashboard.
  - `private static String hash(String text)`: Utility function converting password text into a 64-character hexadecimal SHA-256 string using `java.security.MessageDigest`.

### 4.2. `Voter.java` (Subclass of `User`)
- **Purpose:** Models an eligible voter.
- **Inheritance:** Extends `User` and invokes `super(voterId, name, password)`.
- **Attributes:**
  - `private int age`: Voter's age.
  - `private boolean hasVoted`: Flag initialized to `false`; turns `true` once a ballot is submitted.
- **Core Methods:**
  - `getAge()`, `hasVoted()`, `setHasVoted(boolean hasVoted)`: Standard encapsulated getters/setters.
  - `@Override public void displayMenu()`: Implements the polymorphic method to present the console voter dashboard (View Candidates, Cast Vote, Check Status, Logout).

### 4.3. `Admin.java` (Subclass of `User`)
- **Purpose:** Models the election official / administrator.
- **Inheritance:** Extends `User` and invokes `super(adminId, name, password)`.
- **Core Methods:**
  - `@Override public void displayMenu()`: Implements the admin console dashboard (Add Candidate, View Candidates, View Results, View Statistics, Logout).

### 4.4. `Candidate.java`
- **Purpose:** Represents a political candidate contesting the election.
- **Attributes:**
  - `private int candidateId`: Unique integer ID (1, 2, 3, etc.).
  - `private String name`: Name of the candidate.
  - `private String party`: Political party or organization.
  - `private int voteCount`: Total votes accumulated (starts at 0).
- **Core Methods:**
  - `public void addVote()`: Increments `voteCount` by 1.
  - `@Override public String toString()`: Overrides `Object.toString()` to output formatted details like `1. Aarav Sharma  (Unity Party)`.

### 4.5. `Election.java`
- **Purpose:** Central controller containing all election data structures and business validation rules.
- **Attributes:**
  - `private ArrayList<Voter> voters`: Dynamic list holding all registered voters.
  - `private ArrayList<Candidate> candidates`: Dynamic list of candidates.
  - `private Admin admin`: Pre-configured admin (`admin` / `admin123`).
  - `private int nextCandidateId`: Auto-incrementing identifier counter for newly created candidates.
- **Constructor:** Automatically loads three default demo candidates:
  1. Aarav Sharma (*Unity Party*)
  2. Meera Iyer (*Progress Alliance*)
  3. Kabir Singh (*People's Front*)
- **Core Methods:**
  - `registerVoter(String id, String name, int age, String password)`: Overloaded method that constructs a `Voter` and registers it.
  - `registerVoter(Voter v)`: Overloaded method validating that `v.getAge() >= 18` and that the Voter ID is not already present. Throws `IllegalArgumentException` on failure.
  - `loginVoter(String id, String password)` & `loginAdmin(String id, String password)`: Validates credentials and returns authenticated objects or `null`.
  - `castVote(Voter voter, int candidateId)`: Ensures candidates exist, ensures `voter.hasVoted()` is false, locates the chosen candidate, increments their tally, and marks the voter as having voted.
  - `showResults()`: Prints candidate vote tallies to the console and determines either the single winner or names of all tied candidates.
  - `getWinnerSummary()`: Returns a concise String summary (`WINNER: ...` or `TIE: ...` or `"No votes have been cast yet."`) used by both the console and the web page.
  - `showStatistics()`: Computes total registered voters, total votes cast, and the turnout percentage (`cast * 100.0 / registered`).

### 4.6. `Main.java` (Console Entry Point)
- **Purpose:** Provides a clean command-line user interface.
- **Input Helpers:**
  - `readInt(String prompt)`: Loops until a valid integer is supplied, catching `NumberFormatException` so the program never crashes when users type letters instead of numbers.
  - `readText(String prompt)`: Ensures fields cannot be submitted empty.
- **Execution Flow:** Displays the main menu (Registration, Voter Login, Admin Login, Exit) and drives user sessions through switch-case statements.

### 4.7. `WebMain.java` (Web Server Entry Point)
- **Purpose:** Starts an embedded HTTP server on `http://localhost:8080` using Java's built-in `com.sun.net.httpserver.HttpServer`.
- **How It Works:**
  - Serves `web/index.html` to browsers.
  - Manages session tokens via `Map<String, User> sessions`.
  - Exposes REST-like JSON API endpoints:
    - `/api/register`
    - `/api/login` & `/api/admin-login`
    - `/api/candidates`
    - `/api/status`
    - `/api/vote`
    - `/api/add-candidate`
    - `/api/results`
    - `/api/logout`
  - Reuses the **exact same** `Election`, `Voter`, `Admin`, and `Candidate` classes without changing their OOP logic.

---

## 5. Object-Oriented Programming (OOP) Concepts Demonstrated

| OOP Concept | Definition | Implementation in DigiVoter |
|:---|:---|:---|
| **Abstraction** | Hiding complex internal implementation details and showing only the essential interface to the outside. | `User` is an `abstract class`. It defines the abstract method `displayMenu()`. Subclasses must provide their own implementation, hiding menu differences from the base class. |
| **Encapsulation** | Wrapping variables and methods into a single unit and restricting direct access using access modifiers. | All fields in `User`, `Voter`, `Candidate`, and `Election` are marked `private`. Values are only accessed and modified through public getters and setters (e.g., `getName()`, `addVote()`, `setHasVoted()`). |
| **Inheritance** | Mechanism where a new child class acquires properties and behaviors from an existing parent class. | `Voter extends User` and `Admin extends User`. Both child classes inherit `userId`, `name`, and `passwordHash`, and call `super(...)` to initialize parent fields. |
| **Runtime Polymorphism (Overriding)** | Resolving a method call dynamically at runtime based on the actual object type. | `Voter` and `Admin` each provide their own `@Override public void displayMenu()`. When called on a `User` reference, the appropriate menu is executed. `Candidate` overrides `toString()`. |
| **Compile-time Polymorphism (Overloading)** | Having multiple methods in the same class with the same name but different parameter lists. | In `Election.java`, `registerVoter` is overloaded:<br>1. `registerVoter(String id, String name, int age, String password)`<br>2. `registerVoter(Voter v)` |
| **Constructors & `super`** | Special methods used to initialize objects when created. | Every class has defined constructors. `Voter` and `Admin` use `super(id, name, password)` to initialize the inherited state from `User`. |
| **Collections Framework** | Standard data structures for storing and manipulating groups of objects. | `ArrayList<Voter>` and `ArrayList<Candidate>` in `Election.java` store dynamic records. `HashMap<String, User>` in `WebMain.java` manages active browser sessions. |
| **Exception Handling** | Detecting and handling runtime errors gracefully. | Uses `try-catch` blocks. `Election.java` throws `IllegalArgumentException` (underage voter, duplicate ID) and `IllegalStateException` (already voted, no candidates). `Main.java` handles `NumberFormatException`. |

---

## 6. Security & Election Integrity Rules

1. **Password Hashing (SHA-256):**
   - User passwords are encrypted using SHA-256 via Java's `MessageDigest`.
   - Even if memory or logs are inspected, original passwords cannot be read.
2. **One-Person-One-Vote Rule:**
   - Every voter instance holds a boolean `hasVoted` flag.
   - When attempting to vote, `castVote()` checks `if (voter.hasVoted())`. If true, an `IllegalStateException` is thrown and the vote is rejected.
3. **Age Requirement ($\ge 18$):**
   - In accordance with election regulations, `registerVoter()` checks `if (v.getAge() < 18)`.
   - If underage, registration is stopped with: `"You must be 18 or older to register."`
4. **Duplicate ID Prevention:**
   - The system checks `findVoter(v.getUserId())`.
   - If an existing record has the same ID (case-insensitive), registration fails with: `"This Voter ID is already registered."`
5. **Session Isolation (Web):**
   - When a voter or admin logs in via `WebMain`, a cryptographically random `UUID` session token is generated and mapped to that `User` object, verifying identity on every API request.

---

## 7. User Interfaces & System Workflows

DigiVoter offers two interfaces sharing the same underlying OOP engine:

### 7.1. Console Workflow (`Main.java`)
1. User starts `Main`.
2. Main menu allows:
   - **Voter Registration:** Prompting for ID, Name, Age, Password with instant validation.
   - **Voter Login:** Checking ID and hashed password $\rightarrow$ opening Voter Dashboard $\rightarrow$ viewing candidates, voting, checking status.
   - **Admin Login:** (`admin` / `admin123`) $\rightarrow$ opening Admin Dashboard $\rightarrow$ adding candidates, viewing results, inspecting turnout statistics.
   - **Exit:** Gracefully quitting the app.

### 7.2. Web Browser Workflow (`WebMain.java` + `web/index.html`)
1. User starts `WebMain` and opens `http://localhost:8080`.
2. The browser presents an interactive single-page interface:
   - Registration card with live validation feedback.
   - Separate Voter Login and Admin Login tabs.
   - Interactive voting booth: click candidate cards, confirm ballot, receive a digital confirmation code (e.g., `DV-849201`).
   - Live Admin Dashboard: dynamic percentage bars per candidate, total votes cast, turnout percentage tile, and one-click candidate addition.

---

## 8. Sample Test Cases & Execution Results

### Test Case 1: Underage Registration Attempt
- **Action:** Attempt to register with Age = `17`.
- **Expected Result:** Registration rejected with an error message.
- **Actual Output:** `Registration failed: You must be 18 or older to register.`

### Test Case 2: Duplicate Registration Attempt
- **Action:** Register with ID `V101`, then try registering another user with the same ID `V101`.
- **Expected Result:** Duplicate registration rejected.
- **Actual Output:** `Registration failed: This Voter ID is already registered.`

### Test Case 3: Casting a Ballot & Attempting Double Voting
- **Action:** Voter `V101` votes for Candidate `1` (Aarav Sharma), then immediately tries voting again.
- **First Attempt Output:** `Your vote has been recorded. Thank you for voting!`
- **Second Attempt Output:** `You have already voted. One vote per voter!`

### Test Case 4: Tie Detection in Election Results
- **Scenario:** Candidate 1 receives 1 vote, Candidate 2 receives 1 vote.
- **Result Output:** `IT'S A TIE between: Aarav Sharma, Meera Iyer`
- **Winner Summary:** `TIE: Aarav Sharma (Unity Party), Meera Iyer (Progress Alliance)`

### Test Case 5: Winner Determination
- **Scenario:** Candidate 1 receives 2 votes, Candidate 2 receives 1 vote.
- **Result Output:** `WINNER: Aarav Sharma (Unity Party)`

---

## 9. How to Compile and Run

### Requirements
- **JDK 17 or higher** installed on your system.
- Terminal / Command Prompt / PowerShell.

### Mode A: Running the Console Application
```bash
# 1. Open terminal and navigate to the src folder
cd src

# 2. Compile all Java source files
javac *.java

# 3. Run the console application
java Main
```

### Mode B: Running the Web Application
```bash
# 1. From the src folder, run WebMain
java WebMain

# 2. Open your web browser and go to:
# http://localhost:8080
```
*(Press `Ctrl + C` in the terminal when you wish to stop the server).*

---

## 10. Limitations

1. **In-Memory Storage:**
   Voters, candidate additions, and votes are stored in memory (`ArrayList` and `HashMap`). Restarting the program resets the data back to default demo state.
2. **Single Hardcoded Administrator:**
   The admin account (`admin` / `admin123`) is defined directly inside `Election.java` rather than supporting dynamic admin account creation.
3. **No Polling Hours Scheduler:**
   Voting is open continuously as long as the application is running, without automated start/end timestamps.

---

## 11. Future Scope

1. **Persistent Database Integration:** Connect to a relational database (MySQL or PostgreSQL) or lightweight SQLite via JDBC to retain election records permanently across sessions.
2. **Two-Factor Authentication (2FA):** Integrate email or SMS OTP (One-Time Password) verification before a ballot can be submitted.
3. **Election Session Controls:** Provide Admin controls to officially open, pause, and close the polling window.
4. **Voter Cryptographic Receipt:** Generate a downloadable PDF or cryptographically signed receipt with a unique voting hash for independent auditability.

---

## 12. Conclusion

The **DigiVoter** project successfully demonstrates how Object-Oriented Programming principles can be applied to solve real-world problems like election management. By cleanly encapsulating user data, utilizing inheritance to separate voters and administrators, using polymorphism for dynamic menu interactions, and securing credentials through SHA-256 hashing, the codebase achieves high modularity, security, and reusability. 

The seamless support for both a terminal CLI and a web browser interface without altering core business classes illustrates the true power and elegance of object-oriented software engineering.

