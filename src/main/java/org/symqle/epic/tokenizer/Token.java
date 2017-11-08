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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (line != token.line) return false;
        if (pos != token.pos) return false;
        if (!text.equals(token.text)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = line;
        result = 31 * result + pos;
        result = 31 * result + text.hashCode();
        return result;
    }
}
