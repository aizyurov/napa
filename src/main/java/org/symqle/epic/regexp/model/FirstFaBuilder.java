package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.FirstFaState;

/**
 * Created by aizyurov on 9/27/17.
 */
public interface FirstFaBuilder {
    /**
     * Builds as many states as needed and returns end state.
     * May modify startState
     * @param startState
     * @return
     */
    FirstFaState endState(FirstFaState startState);
}
