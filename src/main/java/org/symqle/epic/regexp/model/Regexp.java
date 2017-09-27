package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

/**
 * @author lvovich
 */
public class Regexp implements NfaBuilder {

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
    public NfaNode1 endState(NfaNode1 startState) {
        return sequence.endState(startState);
    }
}
