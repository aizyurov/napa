package org.symqle.napa.tokenizer;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class PackedDfa<T> {

    private final int characterClassCount;

    // one per character, Character.MAX_VALUE + 1 size
    private final int[] characterClasses;
    // length = nodeCount * classCount. index = node * classCount + class
    private final int[] edges;
    // length = nodeCount. Each position contains token type if this is a final state, null if not final state
    private final List<T> tokenTypes;

    public PackedDfa(final int characterClassCount, final int[] characterClasses, final int[] edges, List<T> tokenTypes) {
        this.characterClassCount = characterClassCount;
        this.characterClasses = characterClasses;
        this.edges = edges;
        this.tokenTypes = tokenTypes;
    }

    int nextState(int currentState, char c) {
        int index = currentState * characterClassCount + characterClasses[c];
        return edges[index];
    }

    T tag(int currentState) {
        return tokenTypes.get(currentState);
    }

    public <U> PackedDfa<U> transform(Function<T, U> tagConverter) {
        return new PackedDfa<>(characterClassCount, characterClasses, edges, tokenTypes.stream().map(x -> x == null ? null : tagConverter.apply(x)).collect(Collectors.toList()));
    }

    public void printStats() {
        System.err.println("Nodes: " + tokenTypes.size());
        System.err.println("Character classes: " + characterClassCount);
        System.err.println("Edges: " + edges.length);
    }

}
