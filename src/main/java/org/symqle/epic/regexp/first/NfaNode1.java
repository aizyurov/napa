package org.symqle.epic.regexp.first;


import org.symqle.epic.regexp.Lexem;

import java.util.*;

/**
 * Created by aizyurov on 9/27/17.
 */
public class NfaNode1 {

    private static List<NfaNode1> allStates = new ArrayList<>();
    protected final Lexem lexem;
    protected final List<Edge> edges = new ArrayList<>();

    private final int index;
    protected Set<NfaNode1> emptyEdges = new HashSet<>();

    public NfaNode1() {
        this(null);
    }

    public NfaNode1(Lexem lexem) {
        this.lexem = lexem;
        index = allStates.size();
        allStates.add(this);
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

    public static int count() {
        return allStates.size();
    }

    public Lexem getLexem() {
        return lexem;
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
