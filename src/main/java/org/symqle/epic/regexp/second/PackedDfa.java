package org.symqle.epic.regexp.second;

/**
 * @author lvovich
 */
public class PackedDfa {

    // one per character, Character.MAX_VALUE + 1 size
    private final int[] characterClasses;
    // length = nodeCount * classCount. index = node * classCount + class
    private final int[] edges;
    // length = nodeCount
    private final TokenTemplate[] tokenTemplates;

    public PackedDfa(final int[] characterClasses, final int[] edges, final TokenTemplate[] tokenTemplates) {
        this.characterClasses = characterClasses;
        this.edges = edges;
        this.tokenTemplates = tokenTemplates;
    }

    public static class TokenTemplate {
        String[] names;
    }
}
