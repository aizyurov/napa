package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.AnyCharacterSet;
import org.symqle.epic.regexp.first.FirstFaNode;

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
    public FirstFaNode endState(FirstFaNode startState) {
        FirstFaNode endState = new FirstFaNode();
        startState.addEdge(new AnyCharacterSet(), endState);
        return endState;
    }
}
