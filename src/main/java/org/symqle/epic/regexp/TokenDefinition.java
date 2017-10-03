package org.symqle.epic.regexp;

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

    public String getPattern() {
        return pattern;
    }

    public T getTag() {
        return tag;
    }
}
