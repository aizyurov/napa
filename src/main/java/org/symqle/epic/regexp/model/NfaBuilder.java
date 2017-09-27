package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

/**
 * Created by aizyurov on 9/27/17.
 */
public interface NfaBuilder {
    /**
     * Builds as many states as needed and returns end state.
     * May modify startState
     * @param startState
     * @return
     */
    NfaNode1 endState(NfaNode1 startState);
}
