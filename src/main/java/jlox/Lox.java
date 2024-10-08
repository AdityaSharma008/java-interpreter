package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        }
        else if(args.length == 1){
            runFile(args[0]);
        }
        else{
            repl();
        }
    }

    private static void runFile(String filePath) throws IOException{
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));
        if(hadError) System.exit(65);
        if(hadRuntimeError) System.exit(70);
    }

    private static void repl() throws IOException{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError) return;

        interpreter.interpret(statements);
    }

    static void error(int line, String msg){
        report(line, "", msg);
    }

    private static void report(int line, String where, String msg){
        System.err.println("[line " + line + "] Error" + where + ": " + msg);
        hadError = true;
    }

    static void error(Token token, String msg){
        if(token.type == TokenType.EOF){
            report(token.line, " at end", msg);
        }
        else{
            report(token.line, " at '" + token.lexeme + "'", msg);
        }
    }

    static void runtimeError(RuntimeError error){
        System.err.println("[line " + error.token.line + "]: " + error.getMessage());
        hadRuntimeError = true;
    }
}
