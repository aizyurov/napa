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
        return type + " (" + quote(text) + ") at "+line+":"+pos;
    }

    private String quote(String source) {
        if (source == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i=0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(c);
            }
        }
        return builder.toString();
    }
}
