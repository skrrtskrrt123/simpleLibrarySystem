# simpleLibrarySystem
An Example of a Simple Java-Based Library Management System. To create a basic library management system that implements object-oriented principles including inheritance, polymorphism, and event handling. The system features dual interfaces for members and librarians, manages multiple types of library items, and handles borrowing transactions through a Java Swing GUI.

# author
Nur Aleesya Najwa Binti Nor Azli

# how to run this project
1. Make sure Java is installed on your machine.
2. Compile all the Java files:
javac *.java
3. Run the program:
java MainLMS

# user instructions
- Log in as either a Member (IDs: A001–A004) or Librarian.

- Member Interface:
* View available items in the library
* Borrow and return items
* View your current loans and due dates
* Check any outstanding late fees

- Librarian Interface:
* View all library items and their status
* View all registered members
* Monitor all current loans in the system

- The system automatically calculates late fees based on item type:
* Books: $1.00 per day
* DVDs: $2.00 per day
* Magazines: $0.50 per day
