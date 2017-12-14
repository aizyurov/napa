package org.symqle.napa.lexer.model;

import org.symqle.napa.lexer.build.*;

import java.util.List;

/**
 * @author lvovich
 */
class IncludeSet implements CharSet {

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

    CharacterSet characterSet() {
        CharacterSet accumulator = CharacterSet.empty();
        for (Range range: ranges) {
            accumulator = CharacterSet.union(range.characterSet(), accumulator);
        }
        return accumulator;
    }
}
