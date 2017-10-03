package org.symqle.epic.lexer.model;

import org.symqle.epic.lexer.build.CharacterSet;
import org.symqle.epic.lexer.build.NfaNode1;

/**
 * @author lvovich
 */
class CharacterPrimary implements Primary {

    private final char theCharacter;

    public CharacterPrimary(final char theCharacter) {
        this.theCharacter = theCharacter;
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
        startState.addEdge(CharacterSet.single(theCharacter), endState);
        return endState;
    }
}
