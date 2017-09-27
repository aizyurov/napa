package org.symqle.epic.regexp;

/**
 * Created by aizyurov on 9/27/17.
 */
public class Lexem {
    private final String source;
    private final boolean meaningiful;

    public Lexem(String source, boolean meaningiful) {
        this.source = source;
        this.meaningiful = meaningiful;
    }

    public String getSource() {
        return source;
    }

    public boolean isMeaningiful() {
        return meaningiful;
    }
}
