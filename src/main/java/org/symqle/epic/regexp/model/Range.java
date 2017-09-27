package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.AbstractCharacterSet;
import org.symqle.epic.regexp.first.CharacterSet;
import org.symqle.epic.regexp.first.FirstFaState;
import org.symqle.epic.regexp.first.RangeCharacterSet;

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

    /**
     * Builds as many states as needed and returns end state.
     * May modify startState
     *
     * @param startState
     * @return
     */
    public AbstractCharacterSet characterSet() {
        return new RangeCharacterSet(from, to);
    }
}
