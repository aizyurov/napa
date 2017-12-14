package org.symqle.napa.lexer.model;

import org.symqle.napa.lexer.build.CharacterSet;

/**
 * Created by aizyurov on 9/26/17.
 */
class Range {
    private final char from;
    private final char to;

    public Range(char from, char to) {
        this.from = from;
        this.to = to;
    }

    public CharacterSet characterSet() {
        return CharacterSet.range(from, to);
    }
}
