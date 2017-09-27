package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;
import org.symqle.epic.regexp.first.SingleCharacterSet;

/**
 * @author lvovich
 */
public class CharacterPrimary implements Primary {

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
        startState.addEdge(new SingleCharacterSet(theCharacter), endState);
        return endState;
    }
}
