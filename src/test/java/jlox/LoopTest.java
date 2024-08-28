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

public class LoopTest {
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

    @DisplayName("Test loop working")
    @ParameterizedTest
    @MethodSource("loopSyntax")
    void loopWorking(String sourceUnderTest){
        helper(sourceUnderTest);

        StringBuilder expected = new StringBuilder();
        for(int i = 0; i < 10; i++){
            expected.append(i);
            expected.append(lineSeparator);
        }

        assertEquals(expected.toString(), outContent.toString());
    }

    private static Stream<Arguments> loopSyntax(){
        return Stream.of(
                Arguments.of("""
                        for (var i = 0; i < 10; i = i + 1){
                            print i;
                        }"""),
                Arguments.of("""
                        var i = 0;
                        while (i < 10){
                            print i;
                            i = i + 1;
                        }"""),
                Arguments.of("""
                        var i = 0;
                        for(; i < 10; i = i + 1){
                            print i;
                        }""")
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
