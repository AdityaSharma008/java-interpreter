package jlox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

public class ConditionalsTest {
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

    @DisplayName("Check dangling else")
    @ParameterizedTest
    @MethodSource("danglingElse")
    void checkDanglingElse(String sourceUnderTest, String expected){
        helper(sourceUnderTest);
        expected += lineSeparator;
        assertEquals(expected, outContent.toString());
    }

    @DisplayName("Check if-else logic")
    @ParameterizedTest
    @MethodSource("ifElse")
    void checkIfElse(String sourceUnderTest, String expected){
        helper(sourceUnderTest);
        expected += lineSeparator;
        assertEquals(expected, outContent.toString());
    }

    private static Stream<Arguments> danglingElse(){
        return Stream.of(
                Arguments.of("if (true) if (false) print \"bad\"; else print \"good\";", "good"),
                Arguments.of("if (true) if (true) print \"bad\"; else print \"good\";", "bad")
        );
    }

    private static Stream<Arguments> ifElse(){
        return Stream.of(
                Arguments.of("if (true) print \"if\"; else print \"else\";", "if"),
                Arguments.of("if (false) print \"if\"; else print \"else\";", "else")
        );
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
