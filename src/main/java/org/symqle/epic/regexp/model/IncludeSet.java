package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.*;

import java.util.List;

/**
 * @author lvovich
 */
public class IncludeSet implements CharSet {

    private final List<Range> ranges;

    public IncludeSet(List<Range> ranges) {
        if (ranges.isEmpty()) {
            throw new IllegalArgumentException("Empty range");
        }
        this.ranges = ranges;
    }

    /**
     * Builds as many states as needed and returns end state.
     * May modify startState
     *
     * @param startState
     * @return
     */
    @Override
    public NfaNode1 endState(NfaNode1 startState) {
        NfaNode1 endState = new NfaNode1();
        CharacterSet accumulator = characterSet();
        startState.addEdge(accumulator, endState);
        return endState;
    }

    AbstractCharacterSet characterSet() {
        AbstractCharacterSet accumulator = new EmptyCharacterSet();
        for (Range range: ranges) {
            accumulator = new UnionSet(range.characterSet(), accumulator);
        }
        return accumulator;
    }
}
