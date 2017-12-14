package org.symqle.napa.lexer.model;

import org.symqle.napa.lexer.build.NfaBuilder;
import org.symqle.napa.lexer.build.NfaNode1;

/**
 * @author lvovich
 */
class Regexp implements NfaBuilder {

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
