package org.symqle.epic.lexer.model;

/**
 * @author lvovich
 */
class LexerToken {

    private final LexerTokenType type;
    private final char value;

    public LexerToken(final LexerTokenType type, final char value) {
        this.type = type;
        this.value = value;
    }

    public LexerTokenType getType() {
        return type;
    }

    public char getValue() {
        return value;
    }
}
