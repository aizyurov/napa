package org.symqle.napa.lexer.build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aizyurov on 9/27/17.
 */
class NfaNode2 {

    private final Set<Integer> tags;
    private final List<Edge> edges = new ArrayList<>();

    public NfaNode2(Set<Integer> tags) {
        this.tags = new HashSet<>(tags);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
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
