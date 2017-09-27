package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

/**
 * Created by aizyurov on 9/27/17.
 */
public class ChoicePrimary implements Primary {
    private final Choice choice;

    public ChoicePrimary(Choice choice) {
        this.choice = choice;
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
        return choice.endState(startState);
    }
}
