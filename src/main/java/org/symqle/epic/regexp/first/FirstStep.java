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

    public FirstFaNode automaton() {
        FirstFaNode startState = new FirstFaNode();
        for (Lexem lexem: lexems) {
            Scanner scanner = new Scanner(lexem.getPattern());
            final FirstFaNode regexpEndState = new RegexpSyntaxTreeBuilder(scanner).regexp().endState(startState);
            FirstFaNode lexemEndState = new FirstFaNode(lexem);
            regexpEndState.addEmptyEdge(regexpEndState);
        }
        return startState;
    }
}
