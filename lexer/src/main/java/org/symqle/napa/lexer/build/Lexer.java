package org.symqle.napa.lexer.build;

import org.symqle.napa.lexer.TokenDefinition;
import org.symqle.napa.lexer.model.Scanner;
import org.symqle.napa.lexer.model.RegexpSyntaxTreeBuilder;
import org.symqle.napa.tokenizer.PackedDfa;

import java.util.List;
import java.util.Map;
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
        return createNfa()
                .removeEmptyEdges()
                .toDfa()
                .pack()
                .transform(this::transformTags);
    }

    private Set<T> transformTags(Set<Integer> tags) {
        final Map<Boolean, Set<Integer>> partitions = tags.stream().collect(Collectors.partitioningBy(t -> tokenDefinitions.get(t).isLiteral(), Collectors.toSet()));
        final Set<Integer> literalTags = partitions.get(true);
        Set<Integer> selectedPartition = literalTags.isEmpty() ? partitions.get(false) : literalTags;
        return selectedPartition.stream().map(t -> tokenDefinitions.get(t).getTag()).collect(Collectors.toSet());
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
