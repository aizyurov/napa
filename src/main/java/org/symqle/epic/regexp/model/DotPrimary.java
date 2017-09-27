package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.AnyCharacterSet;
import org.symqle.epic.regexp.first.FirstFaState;

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
    public FirstFaState endState(FirstFaState startState) {
        FirstFaState endState = new FirstFaState();
        startState.addEdge(new AnyCharacterSet(), endState);
        return endState;
    }
}
