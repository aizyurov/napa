package org.symqle.epic.lexer.build;

import org.symqle.epic.tokenizer.PackedDfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
class Dfa {
    private final List<DfaNode> nodes;
    private final Map<DfaNode, Integer> index;
    private final CharacterClassRegistry registry;

    public Dfa(final List<DfaNode> nodes, final CharacterClassRegistry registry) {
        this.nodes = nodes;
        this.index = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            index.put(nodes.get(i), i);
        }
        this.registry = registry;
    }

    public PackedDfa<Set<Integer>> pack() {
        int[] characterClasses = new int[Character.MAX_VALUE + 1];
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            int classIndex = registry.classOf(c);
            characterClasses[c] = classIndex;
        }
        characterClasses[Character.MAX_VALUE] = registry.classOf(Character.MAX_VALUE);
        int classCount = registry.size();
        int[] edges = new int[nodes.size() * registry.size()];
        Arrays.fill(edges, -1);
        List<Set<Integer>> tagsByNode = new ArrayList<>(nodes.size());
        for (DfaNode node: nodes) {
            for (Map.Entry<Integer, DfaNode> entry: node.getEdges().entrySet()) {
                int toIndex = index.get(entry.getValue());
                edges[index.get(node) * classCount + entry.getKey()] = toIndex;
            }
            Set<Integer> tags = node.getTags();
            tagsByNode.add(tags.isEmpty() ? null : tags);
        }
        return new PackedDfa<>(classCount, characterClasses, edges, tagsByNode);
    }

}
