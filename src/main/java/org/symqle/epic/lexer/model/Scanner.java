package org.symqle.epic.lexer.model;

/**
 * @author lvovich
 */
public class Scanner {

    private final String source;
    private final boolean literal;

    private int pos = 0;

    private LexerToken next;

    public Scanner(final String source, final boolean literal) {
        this.source = source;
        this.literal = literal;
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
                return literal
                        ? new LexerToken(LexerTokenType.CHARACTER, nextChar)
                        : new LexerToken(LexerTokenType.of(nextChar), nextChar);
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

    public String getSource() {
        return source;
    }

    public int getPos() {
        return pos;
    }
}
