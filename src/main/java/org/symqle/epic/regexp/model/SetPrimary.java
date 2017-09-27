package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

/**
 * @author lvovich
 */
public class SetPrimary implements Primary {
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
