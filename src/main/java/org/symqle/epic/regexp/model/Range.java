package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.CharacterSet;

/**
 * Created by aizyurov on 9/26/17.
 */
public class Range {
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
