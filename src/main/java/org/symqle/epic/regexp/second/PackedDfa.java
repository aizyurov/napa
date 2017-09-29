package org.symqle.epic.regexp.second;

/**
 * @author lvovich
 */
public class PackedDfa {

    private final int characterClassCount;

    // one per character, Character.MAX_VALUE + 1 size
    private final int[] characterClasses;
    // length = nodeCount * classCount. index = node * classCount + class
    private final int[] edges;
    // length = nodeCount
    private final int[][] tokenTypes;

    public PackedDfa(final int characterClassCount, final int[] characterClasses, final int[] edges, final int[][] tokenTypes) {
        this.characterClassCount = characterClassCount;
        this.characterClasses = characterClasses;
        this.edges = edges;
        this.tokenTypes = tokenTypes;
    }
}
