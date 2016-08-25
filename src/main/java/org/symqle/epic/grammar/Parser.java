package org.symqle.epic.grammar;

import java.io.IOException;
import java.io.Reader;

import static org.symqle.epic.grammar.TokenType.EOF;
import static org.symqle.epic.grammar.TokenType.LEFT_BRACKET;
import static org.symqle.epic.grammar.TokenType.NT;

/**
 * @author lvovich
 */
public class Parser {

    public SyntaxTree parse(final Reader reader) throws IOException {
        final Tokenizer tokenizer = new Tokenizer(reader);
        return grammar(tokenizer);
    }

    private SyntaxTree grammar(final Tokenizer tokenizer) throws IOException {
        SyntaxNode grammar = new SyntaxNode("grammar");
        final SyntaxTree first = statement(tokenizer);
        if (first == null) {
            Token next = tokenizer.take();
            throw new IllegalArgumentException("Unexpected token " + next);
        }
        grammar.addChild(first);
        for (SyntaxTree statement = statement(tokenizer); statement != null; statement = statement(tokenizer)) {
            grammar.addChild(statement);
        }
        Token eof = tokenizer.take();
        if (eof.getName() != EOF) {
            throw new IllegalArgumentException("Unexpected token " + eof);
        }
        return grammar;
    }

    private SyntaxTree statement(final Tokenizer tokenizer) throws IOException {
        Token token = tokenizer.preview();
        TokenType tokenType = token.getName();
        switch (tokenType) {
            case TOKEN: case IGNORED: {
                SyntaxNode statement = new SyntaxNode("statement");
                statement.addChild(tokenDefinition(tokenizer));
                return statement;
            }
            case NT: {
                SyntaxNode statement = new SyntaxNode("statement");
                statement.addChild(ruleDefinition(tokenizer));
                return statement;
            }
            default:
                return null;
        }
    }

    private SyntaxTree tokenDefinition(final Tokenizer tokenizer) throws IOException {
        SyntaxNode syntaxNode = new SyntaxNode("tokenDefinition");
        syntaxNode.addChild(new SyntaxLeaf(tokenizer.take())); // either TOKEN or IGNORED
        Token preview = tokenizer.preview();
        if (preview.getName().equals(LEFT_BRACKET)) {
            acceptToken(TokenType.LEFT_BRACKET, syntaxNode, tokenizer, false);
            acceptToken(TokenType.NUMBER, syntaxNode, tokenizer, false);
            acceptToken(TokenType.RIGHT_BRACKET, syntaxNode, tokenizer, false);
        }
        acceptToken(TokenType.EQ, syntaxNode, tokenizer, false);
        acceptToken(TokenType.REGEXP, syntaxNode, tokenizer, false);
        acceptToken(TokenType.SEMICOLON, syntaxNode, tokenizer, false);
        return syntaxNode;
    }

    private void acceptToken(final TokenType expectedType, final SyntaxNode syntaxNode, final Tokenizer tokenizer, boolean optional) throws IOException {
        Token nextToken = tokenizer.preview();
        if (nextToken.getName() == expectedType) {
            syntaxNode.addChild(new SyntaxLeaf(tokenizer.take()));
        } else if (!optional) {
            throw new IllegalArgumentException("Unexpected token " + nextToken);
        }
    }

    private SyntaxTree ruleDefinition(final Tokenizer tokenizer) throws IOException {
        SyntaxNode syntaxNode = new SyntaxNode("ruleDefinition");
        acceptToken(NT, syntaxNode, tokenizer, false);
        acceptToken(TokenType.EQ, syntaxNode, tokenizer, false);
        syntaxNode.addChild(variant(tokenizer));
        while (tokenizer.preview().getName() == TokenType.BAR) {
            acceptToken(TokenType.BAR, syntaxNode, tokenizer, false);
            syntaxNode.addChild(variant(tokenizer));
        }
        acceptToken(TokenType.SEMICOLON, syntaxNode, tokenizer, false);
        return syntaxNode;
    }

    private SyntaxTree variant(final Tokenizer tokenizer) throws IOException {
        SyntaxNode syntaxNode = new SyntaxNode("variant");
        while (addSymbol(syntaxNode, tokenizer)) {
            ;
        }
        return syntaxNode;
    }

    private boolean addSymbol(final SyntaxNode syntaxNode, final Tokenizer tokenizer) throws IOException {

        TokenType tokenType = tokenizer.preview().getName();
        switch (tokenType) {
            case NT:
            case LITERAL:
            case TOKEN:
                syntaxNode.addChild(new SyntaxLeaf(tokenizer.take()));
                return true;
            default:
                return false;
        }
    }

}

