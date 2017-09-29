package org.symqle.epic.regexp.first;

import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.parser.RegexpSyntaxTreeBuilder;
import org.symqle.epic.regexp.scanner.Scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aizyurov on 9/27/17.
 */
public class FirstStep {

    private final List<Lexem> lexems = new ArrayList<>();

    public FirstStep(List<Lexem> lexems) {
        this.lexems.addAll(lexems);
    }

    public NfaNode1 automaton() {
        NfaNode1 startState = new NfaNode1();
        for (Lexem lexem: lexems) {
            Scanner scanner = new Scanner(lexem.getPattern());
            final NfaNode1 regexpEndState = new RegexpSyntaxTreeBuilder(scanner).regexp().endState(startState);
            NfaNode1 lexemEndState = new NfaNode1(lexem);
            regexpEndState.addEmptyEdge(lexemEndState);
        }
        return startState;
    }
}
