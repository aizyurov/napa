package org.symqle.napa.lexer.build;

/**
 * Created by aizyurov on 9/27/17.
 */
public interface NfaBuilder {
    /**
     * Builds as many states as needed and returns end state.
     * May modify startState
     * @param startState
     * @return
     */
    NfaNode1 endState(NfaNode1 startState);
}
