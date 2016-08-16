package org.symqle.epic.grammar;

/**
 * @author lvovich
 */
public class Token {

    private final String name;
    private final String value;
    private final int pos;
    private final int line;

    public Token(final String name, final String value, final int pos, final int line) {
        this.name = name;
        this.value = value;
        this.pos = pos;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getPos() {
        return pos;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return name + '(' +value + ") at " +
                line + ":"  + pos;
    }
}
