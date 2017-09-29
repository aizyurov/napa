package org.symqle.epic.regexp;

/**
 * Created by aizyurov on 9/27/17.
 */
public class TokenDefinition {
    private final String pattern;
    private final int type;

    public TokenDefinition(final String pattern, final int type) {
        this.pattern = pattern;
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public int getType() {
        return type;
    }
}
