package org.symqle.epic.regexp.first;

import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.parser.RegexpSyntaxTreeBuilder;
import org.symqle.epic.regexp.scanner.Scanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aizyurov on 9/27/17.
 */
public class FirstStep {

    private final Map<String, Boolean> lexems = new HashMap<>();

    public FirstStep(List<Lexem> lexems) {
        for (Lexem lexem: lexems) {
            this.lexems.put(lexem.getSource(), this.lexems.getOrDefault(lexem.getSource(), false) | lexem.isMeaningiful());
        }
    }

    public FirstFaState automaton() {
        FirstFaState startState = new FirstFaState();
        for (String pattern: lexems.keySet()) {
            Scanner scanner = new Scanner(pattern);
            final FirstFaState regexpEndState = new RegexpSyntaxTreeBuilder(scanner).regexp().endState(startState);
            FirstFaState lexemEndState = new FirstFaState(new Lexem(pattern, lexems.get(pattern)));
            regexpEndState.addEmptyEdge(regexpEndState);
        }
        return startState;
    }
}
