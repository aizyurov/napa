package org.symqle.epic.regexp;

/**
 * Created by aizyurov on 9/27/17.
 */
public class Lexem {
    private final String pattern;
    private final boolean meaningiful;

    public Lexem(String pattern, boolean meaningiful) {
        this.pattern = pattern;
        this.meaningiful = meaningiful;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isMeaningiful() {
        return meaningiful;
    }
}
