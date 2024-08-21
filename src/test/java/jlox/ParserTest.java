package jlox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static final String lineSepartor = System.lineSeparator();

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
    @DisplayName("Test if expression not present")
    void testExpressionPresence(){
        String script = """
                ;
                """;

        helper(script);

        String expectedOutput = "[line 1] Error at ';': Expect expression." + lineSepartor;

        assertEquals(expectedOutput, errContent.toString());
    }

    @Test
    @DisplayName("Test for variable name")
    void testVariableName(){
        String script = """
                var a = 10;
                print a;
                var = 20;""";

        helper(script);

        String expectedOutput = "[line 3] Error at '=': Expect variable name." + lineSepartor;

        assertEquals(expectedOutput, errContent.toString());
    }

    @ParameterizedTest
    @MethodSource("semicolonTests")
    @DisplayName("Test for semicolon after expression")
    void testSemicolons(String script, String after, int line, String temp){
        helper(script);

        String expectedOutput = "[line "+ line + "] Error at '" + temp + "': Expect ';' after " + after + lineSepartor;

        assertEquals(expectedOutput, errContent.toString());
    }

    private static Stream<Arguments> semicolonTests(){
        return Stream.of(
                Arguments.of("""
                        print 10
                        var a = 20;""", "value.", 2, "var"),
                Arguments.of("""
                        var a = 10
                        print 10;
                        """, "variable declaration.", 2, "print")
        );
    }

    void helper(String script){
        Scanner scanner = new Scanner(script);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
    }
}
