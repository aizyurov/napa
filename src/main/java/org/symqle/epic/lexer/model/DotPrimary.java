package org.symqle.epic.lexer.model;

import org.symqle.epic.lexer.build.CharacterSet;
import org.symqle.epic.lexer.build.NfaNode1;

/**
 * @author lvovich
 */
class DotPrimary implements Primary {

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
        startState.addEdge(CharacterSet.any(), endState);
        return endState;
    }
}
