package org.symqle.epic.lexer.build;

import org.symqle.epic.lexer.TokenDefinition;
import org.symqle.epic.lexer.model.Scanner;
import org.symqle.epic.lexer.model.RegexpSyntaxTreeBuilder;
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
        return  tags.stream().map(t -> tokenDefinitions.get(t).getTag()).collect(Collectors.toSet());
    }

    private Nfa1 createNfa() {
        NfaNode1 startState = new NfaNode1();
        for (int i = 0; i < tokenDefinitions.size(); i++) {
            TokenDefinition<T> tokenDefinition = tokenDefinitions.get(i);
            final NfaNode1 regexpEndState = new RegexpSyntaxTreeBuilder(
                    new Scanner(tokenDefinition.getPattern(), tokenDefinition.isLiteral()))
                    .regexp().endState(startState);
            NfaNode1 lexemEndState = new NfaNode1(i);
            regexpEndState.addEmptyEdge(lexemEndState);
        }
        return new Nfa1(startState);
    }

}
