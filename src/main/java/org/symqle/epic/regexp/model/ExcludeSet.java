package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.AbstractCharacterSet;
import org.symqle.epic.regexp.first.NfaNode1;

/**
 * Created by aizyurov on 9/27/17.
 */
public class ExcludeSet implements CharSet {
    private final AbstractCharacterSet includeSet;

    public ExcludeSet(AbstractCharacterSet includeSet) {
        this.includeSet = includeSet;
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
        AbstractCharacterSet characterSet = AbstractCharacterSet.complement(includeSet);
        NfaNode1 endState = new NfaNode1();
        startState.addEdge(characterSet, endState);
        return endState;
    }
}
