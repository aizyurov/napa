package org.symqle.epic.grammar;

/**
 * @author lvovich
 */
public class TokenDefinition {

    private final String name;
    private final boolean ignored;
    private final String regexp;
    private final boolean exactMatch;
    private final int precedence;
    private final int line;
    private final int pos;

    public TokenDefinition(final String name, final boolean ignored, final String regexp, final boolean exactMatch, final int precedence, final int line, final int pos) {
        this.name = name;
        this.ignored = ignored;
        this.regexp = regexp;
        this.exactMatch = exactMatch;
        this.precedence = precedence;
        this.line = line;
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public boolean isIgnored() {
        return ignored;
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }


}
