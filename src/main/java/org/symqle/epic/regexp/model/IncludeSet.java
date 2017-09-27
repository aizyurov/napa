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
    public FirstFaState endState(FirstFaState startState) {
        FirstFaState endState = new FirstFaState();
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
