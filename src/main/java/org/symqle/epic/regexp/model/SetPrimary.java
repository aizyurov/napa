package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.FirstFaNode;

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
    public FirstFaNode endState(FirstFaNode startState) {
        return set.endState(startState);
    }
}
