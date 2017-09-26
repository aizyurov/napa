package org.symqle.epic.regexp.scanner;

import org.symqle.epic.regexp.model.LexerToken;
import org.symqle.epic.regexp.model.LexerTokenType;

import java.io.IOException;

/**
 * @author lvovich
 */
public class Scanner {

    private final String source;

    private int pos = 0;

    private LexerToken next;

    public Scanner(final String source) {
        this.source = source;
    }

    public LexerTokenType preview() {
        return advance().getType();
    }

    public LexerToken advance() {
        if (next == null) {
            next = scan();
        }
        return next;

    }

    public char get() {
        LexerToken token = advance();
        next = null;
        return token.getValue();
    }

    private LexerToken scan() {
        if (pos >= source.length()) {
            return  new LexerToken(LexerTokenType.EOF, '\0');
        }
        char nextChar = source.charAt(pos++);
        switch (nextChar) {
            case '\\':
                return escaped();
            default:
                return new LexerToken(LexerTokenType.of(nextChar), nextChar);
        }
    }

    private LexerToken escaped() {
        if (pos >= source.length()) {
            return new LexerToken(LexerTokenType.CHARACTER, '\\');
        }
        char nextChar = source.charAt(pos++);
        char value;
        switch (nextChar) {
            case 'n':
                value = '\n';
                break;
            case 'r':
                value = '\r';
                break;
            case 't':
                value = '\t';
                break;
            default:
                value = nextChar;
        }
        return new LexerToken(LexerTokenType.CHARACTER, value);
    }

}
