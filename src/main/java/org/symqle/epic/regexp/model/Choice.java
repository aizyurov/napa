package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

import java.util.List;

/**
 * @author lvovich
 */
public class Choice implements NfaBuilder {

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
    public NfaNode1 endState(NfaNode1 startState) {
        final NfaNode1 endState = new NfaNode1();
        for (Sequence variant: variants) {
            variant.endState(startState).addEmptyEdge(endState);
        }
        return endState;
    }
}
