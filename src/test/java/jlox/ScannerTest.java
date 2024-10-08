package jlox;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("Scanner")
@DisplayName("ScannerTest")
class ScannerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

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

    @Tag("Scanner")
    @ParameterizedTest
    @MethodSource("keywordTestCases")
    @DisplayName("Check that reserved keywords are identified correctly")
    void checkReservedKeyword(String sourceUnderTest, TokenType tokenType) {
        // given
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        // when
        List<Token> testTokens = scannerUnderTest.scanTokens();
        List<Token> expectedTokens = new ArrayList<>();

        expectedTokens.add(new Token(tokenType, sourceUnderTest, null, 1));
        expectedTokens.add(new Token(TokenType.EOF,"", null, 1));

        // then
        assertThat(testTokens).usingRecursiveComparison().isEqualTo(expectedTokens);
    }

    @ParameterizedTest
    @MethodSource("tokenTestCases")
    @DisplayName("Test for tokens")
    void checkTokens(String sourceUnderTest, TokenType tokenType) {
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        List<Token> testTokens = scannerUnderTest.scanTokens();
        List<Token> expectedTokens = new ArrayList<>();

        expectedTokens.add(new Token(tokenType, sourceUnderTest, null, 1));
        expectedTokens.add(new Token(TokenType.EOF, "", null, 1));

        assertThat(testTokens).usingRecursiveComparison().isEqualTo(expectedTokens);
    }

    @ParameterizedTest
    @MethodSource("identifierTestCases")
    @DisplayName("Check that Identifiers are recognized correctly")
    void checkIdentifiers(String sourceUnderTest) {
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        List<Token> testTokens = scannerUnderTest.scanTokens();
        List<Token> expectedTokens = new ArrayList<>();

        expectedTokens.add(new Token(TokenType.IDENTIFIER, sourceUnderTest, null, 1));
        expectedTokens.add(new Token(TokenType.EOF, "", null, 1));

        assertThat(testTokens).usingRecursiveComparison().isEqualTo(expectedTokens);
    }

    @ParameterizedTest
    @MethodSource("commentTestCases")
    @DisplayName("Check that comments are ignored")
    void checkComments(String sourceUnderTest, int numLines) {
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        List<Token> testTokens = scannerUnderTest.scanTokens();
        List<Token> expectedTokens = new ArrayList<>();

        expectedTokens.add(new Token(TokenType.EOF, "", null, numLines));

        assertThat(testTokens).usingRecursiveComparison().isEqualTo(expectedTokens);
    }

    @ParameterizedTest
    @MethodSource("numberTestCases")
    @DisplayName("Check if numbers are recognized properly")
    void checkNumbers(String sourceUnderTest) {
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        List<Token> testTokens = scannerUnderTest.scanTokens();
        List<Token> expectedTokens = new ArrayList<>();

        expectedTokens.add(new Token(TokenType.NUMBER, sourceUnderTest, Double.parseDouble(sourceUnderTest), 1));
        expectedTokens.add(new Token(TokenType.EOF, "", null, 1));

        assertThat(testTokens).usingRecursiveComparison().isEqualTo(expectedTokens);
    }

    @ParameterizedTest
    @MethodSource("stringTestCases")
    @DisplayName("Check if String are recognized properly")
    void checkString(String sourceUnderTest, int numLine) {
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        List<Token> testTokens = scannerUnderTest.scanTokens();
        List<Token> expectedTokens = new ArrayList<>();

        expectedTokens.add(new Token(TokenType.STRING, sourceUnderTest, removeQuotes(sourceUnderTest), numLine));
        expectedTokens.add(new Token(TokenType.EOF, "", null, numLine));

        assertThat(testTokens).usingRecursiveComparison().isEqualTo(expectedTokens);
    }

    @Test
    @DisplayName("Check illegal string")
    void checkWrongString(){
        String wrong = "\"I am a string with no closing quote.";
        Scanner scannerUnderTest = new Scanner(wrong);

        scannerUnderTest.scanTokens();

        String lineSeparator = System.lineSeparator();
        assertEquals("[line 1] Error: Unterminated String." + lineSeparator, errContent.toString());
    }

    @DisplayName("Check for wrong characters")
    @ParameterizedTest
    @MethodSource("wrongCharactersList")
    void checkWrongCharacters(String sourceUnderTest){
        Scanner scannerUnderTest = new Scanner(sourceUnderTest);

        scannerUnderTest.scanTokens();

        String lineSeparator = System.lineSeparator();
        assertEquals("[line 1] Error: Unexpected character." + lineSeparator, errContent.toString());
    }

    // Method source for the parameterized test
    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> keywordTestCases() {
        return Stream.of(
                Arguments.of("and", TokenType.AND),
                Arguments.of("class", TokenType.CLASS),
                Arguments.of("else", TokenType.ELSE),
                Arguments.of("false", TokenType.FALSE),
                Arguments.of("for", TokenType.FOR),
                Arguments.of("fun", TokenType.FUN),
                Arguments.of("if", TokenType.IF),
                Arguments.of("null", TokenType.NULL),
                Arguments.of("print", TokenType.PRINT),
                Arguments.of("return", TokenType.RETURN),
                Arguments.of("super", TokenType.SUPER),
                Arguments.of("this", TokenType.THIS),
                Arguments.of("true", TokenType.TRUE),
                Arguments.of("var", TokenType.VAR),
                Arguments.of("while", TokenType.WHILE)
        );
    }

    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> tokenTestCases(){
        return Stream.of(
                Arguments.of("(", TokenType.LEFT_PAREN),
                Arguments.of(")", TokenType.RIGHT_PAREN),
                Arguments.of("{", TokenType.LEFT_BRACE),
                Arguments.of("}", TokenType.RIGHT_BRACE),
                Arguments.of(",", TokenType.COMMA),
                Arguments.of(".", TokenType.DOT),
                Arguments.of("-", TokenType.MINUS),
                Arguments.of("+", TokenType.PLUS),
                Arguments.of("*", TokenType.STAR),
                Arguments.of("/", TokenType.SLASH),
                Arguments.of("!", TokenType.BANG),
                Arguments.of("!=", TokenType.BANG_EQUAL),
                Arguments.of("<", TokenType.LESS),
                Arguments.of("<=", TokenType.LESS_EQUAL),
                Arguments.of(">", TokenType.GREATER),
                Arguments.of(">=", TokenType.GREATER_EQUAL)
        );
    }

    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> identifierTestCases(){
        return Stream.of(
                Arguments.of("health"),
                Arguments.of("damage"),
                Arguments.of("magic"),
                Arguments.of("_underscoreIdentifier")
        );
    }

    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> commentTestCases(){
        return Stream.of(
                Arguments.of("//I am a single line comment.", 1),
                Arguments.of("/*I am a multiline line comment.\nScanner should ignore all my lines*/", 2)
        );
    }

    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> numberTestCases(){
        return Stream.of(
                Arguments.of("123"),
                Arguments.of("123.456"),
                Arguments.of("0123"),
                Arguments.of("12345567.123124")
        );
    }

    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> stringTestCases(){
        return Stream.of(
                Arguments.of("\"I am a String.\"", 1),
                Arguments.of("\"I am a multiline string.\nThis line is on new row.\nThis is on third row\"", 3),
                Arguments.of("\"\nI start from second line.\"", 2)
        );
    }

    @org.jetbrains.annotations.NotNull
    private static Stream<Arguments> wrongCharactersList(){
        return Stream.of(
                Arguments.of("@"),
                Arguments.of("$"),
                Arguments.of("^"),
                Arguments.of("`")
        );
    }

    private String removeQuotes(String s){
        return s.substring(1, s.length() - 1);
    }
}
