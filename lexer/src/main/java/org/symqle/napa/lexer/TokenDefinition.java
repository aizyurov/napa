package org.symqle.napa.lexer;

/**
 * Created by aizyurov on 9/27/17.
 */
public class TokenDefinition<T> {
    private final String pattern;
    private final T tag;
    private boolean literal;

    public TokenDefinition(final String pattern, final T tag) {
        this(pattern, tag, false);
    }

    public TokenDefinition(final String pattern, final T tag, final boolean literal) {
        this.pattern = pattern; // .replaceAll("\\\"", "\"").replaceAll("\\\'", "\'");
        this.tag = tag;
        this.literal = literal;
    }

    public static <T> TokenDefinition<T> def(final String pattern, final T tag) {
        return new TokenDefinition<>(pattern, tag);
    }

    public String getPattern() {
        return pattern;
    }

    public T getTag() {
        return tag;
    }

    public boolean isLiteral() {
        return literal;
    }
}
