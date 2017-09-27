package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.ComplementSet;
import org.symqle.epic.regexp.first.FirstFaState;

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
    public FirstFaState endState(FirstFaState startState) {
        final ComplementSet characterSet = new ComplementSet(includeSet.characterSet());
        FirstFaState endState = new FirstFaState();
        startState.addEdge(characterSet, endState);
        return endState;
    }
}
