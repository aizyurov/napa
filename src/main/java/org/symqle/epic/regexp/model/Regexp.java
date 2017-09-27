package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.FirstFaState;

/**
 * @author lvovich
 */
public class Regexp implements FirstFaBuilder {

    private final Choice sequence;

    public Regexp(final Choice sequence) {
        this.sequence = sequence;
    }

    /**
     * Builds as many states as needed and returns end state.
     *
     * @param startState
     * @return
     */
    @Override
    public FirstFaState endState(FirstFaState startState) {
        return sequence.endState(startState);
    }
}
