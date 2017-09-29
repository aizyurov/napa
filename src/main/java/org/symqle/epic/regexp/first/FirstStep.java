package org.symqle.epic.regexp.first;

import org.symqle.epic.regexp.TokenDefinition;
import org.symqle.epic.regexp.parser.RegexpSyntaxTreeBuilder;
import org.symqle.epic.regexp.scanner.Scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aizyurov on 9/27/17.
 */
public class FirstStep {

    private final List<TokenDefinition> tokenDefinitions = new ArrayList<>();

    public FirstStep(List<String> tokens, List<String> ignored) {
        for (int i = 0; i < tokens.size(); i++) {
            tokenDefinitions.add(new TokenDefinition(tokens.get(i), i));
        }
        for (int i = 0; i < ignored.size(); i++) {
            tokenDefinitions.add(new TokenDefinition(tokens.get(i), -1));
        }
    }

    public NfaNode1 automaton() {
        NfaNode1 startState = new NfaNode1();
        for (TokenDefinition tokenDefinition : tokenDefinitions) {
            Scanner scanner = new Scanner(tokenDefinition.getPattern());
            final NfaNode1 regexpEndState = new RegexpSyntaxTreeBuilder(scanner).regexp().endState(startState);
            NfaNode1 lexemEndState = new NfaNode1(tokenDefinition);
            regexpEndState.addEmptyEdge(lexemEndState);
        }
        return startState;
    }
}
