# java-interpreter

This project is a simple interpreter written in Java, based on concepts learned from the book "Crafting Interpreters" by Robert Nystrom. 

## Features

- **Scanner**: Tokenizes input into recognizable elements.
- **Parser**: Converts tokens into an Abstract Syntax Tree (AST).
- **Interpreter**: Evaluates expressions and handles `print` statements.

## Installation 

1. **Clone the repository**:
   ```bash
   git clone https://github.com/AdityaSharma008/java-interpreter.git
   cd java-interpreter
   ```

2. **Build the project**:
   - Using Maven:
      ```bash
      mvn package
      ```

   - Without Maven:
      ```bash
      cd src/main/java/
      javac -d bin src/main/java/jlox/*.java
      ```

## Usage

1. **To run REPL**:
   - Using Maven:
      ```bash
      java -cp target/java-interpreter-1.0-SNAPSHOT.jar
      ```

   - Without Maven:
      ```bash
      java -cp bin jlox.Lox
      ```

2. **To run file**:
   ```java
   print "Hello, World!"; //Output: Hello, World!
   var a = 10;
   var b = 20;
   print a+b;             //Output: 30
   ```

   To run the above code, save it in file(e.g., `sript.txt`) and execute:
   - Using Maven:
      ```bash
      java -cp target/java-interpreter-1.0-SNAPSHOT.jar script.txt
      ```

   - Without Maven:
      ```bash
      java -cp bin jlox.Lox filename
      ```

3. **Testing**
Testing requires Maven to handle JUnit dependencies. To run the tests:
   ```bash
   mvn test
   ```