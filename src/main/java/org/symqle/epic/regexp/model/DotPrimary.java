package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.AbstractCharacterSet;
import org.symqle.epic.regexp.first.NfaNode1;

/**
 * @author lvovich
 */
public class DotPrimary implements Primary {

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
        startState.addEdge(AbstractCharacterSet.any(), endState);
        return endState;
    }
}
