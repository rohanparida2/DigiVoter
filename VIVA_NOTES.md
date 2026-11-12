# DigiVoter - Viva Notes

## Say it in one line
- **Abstraction:** User is abstract; it hides common details and forces subclasses to implement displayMenu().
- **Inheritance:** Voter and Admin extend User and reuse userId, name and password.
- **Polymorphism:** `user.displayMenu()` shows a different menu depending on whether the object is a Voter or an Admin.
- **Encapsulation:** all fields are private; we access them only through methods.
- **Overloading:** registerVoter(id, name, age, password) and registerVoter(Voter v) - same name, different parameters.
- **Constructors:** initialise objects; Voter calls super(...) to set up the User part.

## Likely questions
1. Why is User abstract? Because a plain "User" never exists on its own; only Voters and Admins do.
2. How do you stop double voting? Voter has a hasVoted flag; Election.castVote checks it and throws an exception.
3. What happens on a tie? showResults() collects every candidate with the maximum votes and prints a tie.
4. Why ArrayList? It grows dynamically and is simple to search and loop through.
5. Why private fields? Encapsulation: outside code cannot change data without going through our checks.
6. How are passwords protected? Hashed with SHA-256 in User; the plain password is never stored.
7. Overloading vs overriding? Overloading: same name, different parameters (registerVoter). Overriding: subclass replaces parent method (displayMenu).
8. Where is exception handling? readInt() catches NumberFormatException; Election throws exceptions that Main catches.
9. What is super()? Calls the parent constructor.
10. Limitations? In-memory data only; not secure enough for real elections.

## 4-minute demo order
Register a voter (try age 17 first) -> try duplicate ID -> login with wrong password -> login correctly -> view candidates -> vote -> try voting again -> logout -> admin login -> results -> statistics -> add candidate -> exit.
