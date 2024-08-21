package jlox;

import java.util.ArrayList;
import java.util.List;

import static jlox.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException{

    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens){
        this.tokens = tokens;
    }

    List<Stmt> parse(){
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration(){
        try{
            if(match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error){
            synchronize();
            return null;
        }
    }

    //varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;
    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    //statement -> print | expression
    private Stmt statement(){
        if(match(PRINT)) return printStatement();
        if (match(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    // printStatement -> print expression ;
    private Stmt printStatement(){
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement(){
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block(){
        List<Stmt> statements = new ArrayList<>();

        while(!check(RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    //expression -> equality
    private Expr expression(){
        return assignment();
    }

    //assignment -> IDENTIFIER '=' expression | equality
    private Expr assignment(){
        Expr expr = equality();

        if(match(EQUAL)){
            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable){
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    //equality -> comparison ((!= | ==) comparison)*;
    private Expr equality(){
        Expr expr = comparison();

        while(match(BANG_EQUAL, EQUAL_EQUAL)){
            Token operator = previous(); //since match consumes the token
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //comparison -> term (( > | >= | < | <= ) term)*;
    private Expr comparison(){
        Expr expr = term();

        while(match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //term -> factor (( - | + ) factor)*;
    private Expr term(){
        Expr expr = factor();

        while(match(MINUS, PLUS)){
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //factor -> unary (( / | *) unary)*;
    private Expr factor(){
        Expr expr = unary();

        while(match(SLASH, STAR)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //unary -> ( ! | - ) unary | primary;
    private Expr unary(){
        if(match(BANG, MINUS)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    //primary -> NUMBER | STRING | "true" | "false" | "null" | "(" expression ")";
    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NULL)) return new Expr.Literal(null);

        if(match(NUMBER, STRING)){
            return new Expr.Literal(previous().literal);
        }

        if(match(LEFT_PAREN)){
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        if(match(IDENTIFIER)){
            return new Expr.Variable(previous());
        }

        throw error(peek(), "Expect expression.");
    }

    //Matches and consumes tokens
    private boolean match(TokenType... types){
        for(TokenType type: types){
            if(check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }

    //consumes token
    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd(){
        return peek().type == EOF;
    }

    //current token yet to be consumed
    private Token peek(){
        return tokens.get(current);
    }

    //returns most recently consumed token
    private Token previous(){
        return tokens.get(current - 1);
    }


    private Token consume(TokenType type, String msg){
        if(check(type)) return advance();

        throw error(peek(), msg);
    }

    private ParseError error(Token token, String msg){
        Lox.error(token, msg);
        return new ParseError();
    }

    private void synchronize(){
        advance();

        while(!isAtEnd()){
            if(previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> {
                    return;
                }
            }

            advance();
        }
    }
}