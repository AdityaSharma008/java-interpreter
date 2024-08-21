package jlox;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssignmentTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static final String lineSeparator = System.lineSeparator();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Test right associativity")
    void testAssociativity(){
        String script = """
                var a = "a";
                var b = "b";
                var c = "c";
                                
                a = b = c;
                print a; // expect: c
                print b; // expect: c
                print c; // expect: c
                """;

        helper(script);

        String expectedOutput = "c" + lineSeparator + "c" + lineSeparator + "c" + lineSeparator;

        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    @DisplayName("Throw error for undefined variable")
    void testUndefinedVariable(){
        String script = """
                undef = 123;
                """;

        helper(script);

        String expectedOutput = "[line 1]: Undefined Variable 'undef'." + lineSeparator;

        assertEquals(expectedOutput, errContent.toString());
    }

    @Test
    @DisplayName("Check scope")
    void testScope(){
        String script = """
                var a = 10;
                {
                    print a;
                    var a = 20;
                    print a;
                }
                print a;
                """;

        helper(script);

        String expectedOutput = "10" + lineSeparator + "20" + lineSeparator + "10" + lineSeparator;

        assertEquals(expectedOutput, outContent.toString());
    }

    void helper(String script){
        Scanner scanner = new Scanner(script);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(statements);
    }
}
