package org.symqle.epic.tokenizer;

/**
 * @author lvovich
 */
public class Token<T> {

    private final T type;
    private final int line;
    private final int pos;
    private final String text;

    public Token(final T type, final int line, final int pos, final String text) {
        this.type = type;
        this.line = line;
        this.pos = pos;
        this.text = text;
    }

    public T getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return type + " (" + text + ") at "+line+":"+pos;
    }
}
