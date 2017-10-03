package org.symqle.epic.lexer;

/**
 * Created by aizyurov on 9/27/17.
 */
public class TokenDefinition<T> {
    private final String pattern;
    private final T tag;

    public TokenDefinition(final String pattern, final T tag) {
        this.pattern = pattern;
        this.tag = tag;
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
}
