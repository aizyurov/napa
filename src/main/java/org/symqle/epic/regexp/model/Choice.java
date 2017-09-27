package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.FirstFaNode;

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
    public FirstFaNode endState(FirstFaNode startState) {
        final FirstFaNode endState = new FirstFaNode();
        for (Sequence variant: variants) {
            variant.endState(startState).addEmptyEdge(endState);
        }
        return endState;
    }
}
