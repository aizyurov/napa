package org.symqle.napa.lexer.build;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aizyurov on 9/27/17.
 */
public class NfaNode1 {

    private final int tag;
    private final List<Edge> edges = new ArrayList<>();

    private Set<NfaNode1> emptyEdges = new HashSet<>();

    public NfaNode1() {
        this(-1);
    }

    public NfaNode1(int tag) {
        this.tag = tag;
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Set<NfaNode1> getEmptyEdges() {
        return Collections.unmodifiableSet(emptyEdges);
    }

    public void addEmptyEdge(NfaNode1 to) {
        emptyEdges.add(to);
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    public int getTag() {
        return tag;
    }

    public void addEdge(CharacterSet characterSet, NfaNode1 to) {
        edges.add(new Edge(characterSet, to));
    }

    /**
    * Created by aizyurov on 9/27/17.
    */
    public static class Edge {
        private final CharacterSet characterSet;
        private final NfaNode1 to;

        public Edge(CharacterSet characterSet, NfaNode1 to) {
            this.characterSet = characterSet;
            this.to = to;
        }

        public CharacterSet getCharacterSet() {
            return characterSet;
        }

        public NfaNode1 getTo() {
            return to;
        }
    }

}
