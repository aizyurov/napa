package org.symqle.napa.lexer.build;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class NfaNode3 {
    private final Set<Integer> tags;

    private final Map<NfaNode3, CharacterSet> edges = new HashMap<>();

    public NfaNode3(final Set<Integer> tags) {
        this.tags = new HashSet<>(tags);
    }

    public void addEdge(NfaNode3 target, CharacterSet characterSet) {
        CharacterSet existing = this.edges.getOrDefault(target, CharacterSet.empty());
        CharacterSet union = CharacterSet.union(existing, characterSet);
        edges.put(target, union);
    }

    public Map<NfaNode3, CharacterSet> getEdges() {
        return Collections.unmodifiableMap(edges);
    }

    public Set<Integer> getTags() {
        return Collections.unmodifiableSet(tags);
    }
}
