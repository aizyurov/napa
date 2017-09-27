package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.FirstFaState;

import java.util.List;

/**
 * @author lvovich
 */
public class Choice implements FirstFaBuilder {

    private final List<Sequence> variants;

    public Choice(final List<Sequence> variants) {
        this.variants = variants;
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
        final FirstFaState endState = new FirstFaState();
        for (Sequence variant: variants) {
            variant.endState(startState).addEmptyEdge(endState);
        }
        return endState;
    }
}
