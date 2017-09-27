package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

/**
 * @author lvovich
 */
public class Repeat implements NfaBuilder {

    private final Primary primary;
    private final Repetitions repetitions;

    public Repeat(final Primary primary, final Repetitions repetitions) {
        this.primary = primary;
        this.repetitions = repetitions;
    }

    public static enum Repetitions {
        ZERO_OR_MORE,
        ZERO_OR_ONE,
        ONE_OR_MORE,
        EXACTLY_ONE
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
        final NfaNode1 endState = primary.endState(startState);
        switch (repetitions) {
            case ZERO_OR_MORE:
                startState.addEmptyEdge(endState);
                endState.addEmptyEdge(startState);
                break;
            case ONE_OR_MORE:
                endState.addEmptyEdge(startState);
                break;
            case ZERO_OR_ONE:
                startState.addEmptyEdge(endState);
                break;
            case EXACTLY_ONE:
                // do nothing
        }
        return endState;
    }
}