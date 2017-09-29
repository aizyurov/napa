package org.symqle.epic.regexp.second;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class DfaNode {

    private Map<Integer, DfaNode> edges = new HashMap<>();

    private final Set<Integer> labels;

    public DfaNode(final Set<Integer> labels) {
        this.labels = labels;
    }

    public void addEdge(Integer characterClass, DfaNode to) {
        DfaNode existing = edges.put(characterClass, to);
        if (existing != null && !existing.equals(to)) {
            throw new IllegalArgumentException("This is DFA, edge for " + characterClass + " already exists");
        }
    }

    public Map<Integer, DfaNode> getEdges() {
        return Collections.unmodifiableMap(edges);
    }

    public Set<Integer> getLabels() {
        return labels;
    }
}
