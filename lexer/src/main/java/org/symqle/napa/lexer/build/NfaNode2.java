package org.symqle.napa.lexer.build;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 9/27/17.
 */
class NfaNode2 {

    private final Set<Integer> tags;
    private final Map<NfaNode2, CharacterSet> edges = new HashMap<>();

    public NfaNode2(Set<Integer> tags) {
        this.tags = new HashSet<>(tags);
    }

    public void addEdge(Edge edge) {

        final CharacterSet characterSet = edges.get(edge.getTo());
        if (characterSet == null) {
            edges.put(edge.getTo(), edge.getCharacterSet());
        } else {
            edges.put(edge.getTo(), CharacterSet.union(characterSet, edge.getCharacterSet()));
        }
    }

    public List<Edge> getEdges() {
        return edges.entrySet().stream().map(e -> new Edge(e.getValue(), e.getKey())).collect(Collectors.toList());
    }

    public Set<Integer> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public static class Edge {
        private final CharacterSet characterSet;
        private final NfaNode2 to;

        public Edge(CharacterSet characterSet, NfaNode2 to) {
            this.characterSet = characterSet;
            this.to = to;
        }

        public CharacterSet getCharacterSet() {
            return characterSet;
        }

        public NfaNode2 getTo() {
            return to;
        }
    }
}
