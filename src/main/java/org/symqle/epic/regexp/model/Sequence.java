package org.symqle.epic.regexp.model;

import org.symqle.epic.regexp.first.NfaNode1;

import java.util.List;

/**
 * @author lvovich
 */
public class Sequence implements NfaBuilder {

    private final List<Repeat> repetitions;

    public Sequence(final List<Repeat> repetitions) {
        if (repetitions.isEmpty()) {
            throw new IllegalArgumentException("Empty sequence");
        }
        this.repetitions = repetitions;
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
        NfaNode1 current = startState;
        for (Repeat repeat: repetitions) {
            current = repeat.endState(current);
        }
        return current;
    }
}
