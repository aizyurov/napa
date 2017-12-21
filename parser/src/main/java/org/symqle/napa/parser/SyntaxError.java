package org.symqle.napa.parser;

/**
 * @author lvovich
 */
public class SyntaxError {

    private final String message;
    private final String file;
    private final int line;
    private final int pos;

    public SyntaxError(final String message, final String file, final int line, final int pos) {
        this.message = message;
        this.file = file;
        this.line = line;
        this.pos = pos;
    }

    public String getMessage() {
        return message;
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return String.format("%s at %s:%d:%d", getMessage(), file, getLine(), getPos());
    }
}
