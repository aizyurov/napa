package org.symqle.epic.lexer.model;

import org.symqle.epic.lexer.build.NfaNode1;

/**
 * @author lvovich
 */
class SetPrimary implements Primary {
    private final CharSet set;

    public SetPrimary(final CharSet set) {
        this.set = set;
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
        return set.endState(startState);
    }
}
