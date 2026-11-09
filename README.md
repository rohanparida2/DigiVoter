# DigiVoter - Java Digital Voting System (OOP Mini Project)

A console-based voting application built in Java to demonstrate Object-Oriented Programming.
**Educational simulation. Not for real elections.**

## Features
- Voter registration (age 18+, no duplicate Voter ID)
- Voter login, view candidates, cast one vote, view voting status
- Admin login (admin / admin123), add and view candidates
- Election results with winner or tie detection, and voting statistics (turnout %)
- Input validation and exception handling for wrong or non-numeric input
- Passwords stored as SHA-256 hashes, not plain text
- 3 fictional demo candidates pre-loaded

## How to run
```
cd src
javac *.java
java Main
```
Requires JDK 17 or newer.

## Run the web version (browser UI on localhost)
```
cd src
javac *.java
java WebMain
```
Then open **http://localhost:8080** in your browser. Keep the `web` folder next to `src`.
In VS Code you can also open `WebMain.java` and click Run. Stop the server with Ctrl+C.
The browser page (web/index.html) talks to the same Java classes through a small built-in HTTP server (no extra libraries).

## Class structure
```
User (abstract)
 |-- Voter  (age, hasVoted)
 |-- Admin
Candidate (id, name, party, voteCount)
Election  (ArrayList<Voter>, ArrayList<Candidate>, voting logic)
Main      (console menus and entry point)
WebMain   (web server: connects the browser UI to the same classes)
```

## OOP concepts used
| Concept | Where |
|---|---|
| Class and Object | Voter, Candidate, Admin, Election |
| Encapsulation | Private fields with getters/setters in every class |
| Inheritance | Voter and Admin extend User |
| Polymorphism | displayMenu() overridden in Voter and Admin; called on the same User type |
| Abstraction | User is abstract with abstract displayMenu() |
| Constructors | Every class, plus super(...) calls in Voter and Admin |
| Method overloading | Election.registerVoter(...) has two versions |
| Collections | ArrayList for voters and candidates |
| Exception handling | try/catch in Main; IllegalArgumentException / IllegalStateException in Election |

## Limitations
Data is kept in memory only and is lost when the program closes. A real system would need a database, proper authentication, and audit logs.

## Future scope
File or database storage, GUI or web interface, election open/close status.
