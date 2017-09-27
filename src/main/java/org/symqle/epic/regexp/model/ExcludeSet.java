package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.ComplementSet;
import org.symqle.epic.regexp.first.FirstFaNode;

/**
 * Created by aizyurov on 9/27/17.
 */
public class ExcludeSet implements CharSet {
    private final IncludeSet includeSet;

    public ExcludeSet(IncludeSet includeSet) {
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
    public FirstFaNode endState(FirstFaNode startState) {
        final ComplementSet characterSet = new ComplementSet(includeSet.characterSet());
        FirstFaNode endState = new FirstFaNode();
        startState.addEdge(characterSet, endState);
        return endState;
    }
}
