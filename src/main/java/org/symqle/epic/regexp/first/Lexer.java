package org.symqle.epic.regexp.first;

import org.symqle.epic.regexp.TokenDefinition;
import org.symqle.epic.regexp.model.RegexpSyntaxTreeBuilder;
import org.symqle.epic.regexp.scanner.Scanner;
import org.symqle.epic.tokenizer.PackedDfa;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class Lexer<T> {

    private final List<TokenDefinition<T>> tokenDefinitions;


    public Lexer(final List<TokenDefinition<T>> tokenDefinitions) {
        this.tokenDefinitions = tokenDefinitions;
    }

    public PackedDfa<Set<T>> compile() {
        long start = System.currentTimeMillis();
        try {
            return createNfa()
                    .removeEmptyEdges()
                    .toDfa()
                    .pack()
                    .transform(this::transformTags);
        } finally {
            System.out.println("Compilation time: " + (System.currentTimeMillis() - start));
        }
    }

    private Set<T> transformTags(Set<Integer> tags) {
        if (tags.isEmpty()) {
            return null;
        } else {
            return  tags.stream().map(t -> tokenDefinitions.get(t).getTag()).collect(Collectors.toSet());
        }
    }

    private Nfa1 createNfa() {
        NfaNode1 startState = new NfaNode1();
        for (int i = 0; i < tokenDefinitions.size(); i++) {
            Scanner scanner = new Scanner(tokenDefinitions.get(i).getPattern());
            final NfaNode1 regexpEndState = new RegexpSyntaxTreeBuilder(scanner).regexp().endState(startState);
            NfaNode1 lexemEndState = new NfaNode1(i);
            regexpEndState.addEmptyEdge(lexemEndState);
        }
        return new Nfa1(startState);
    }

}
