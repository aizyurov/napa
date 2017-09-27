package org.symqle.epic.regexp.first;


import org.symqle.epic.regexp.Edge1;
import org.symqle.epic.regexp.Lexem;

import java.util.*;

/**
 * Created by aizyurov on 9/27/17.
 */
public class FirstFaNode extends NfaNode {

    private static List<FirstFaNode> allStates = new ArrayList<>();
    protected final Lexem lexem;
    protected final List<Edge1> edges = new ArrayList<>();

    private final int index;
    protected Set<FirstFaNode> emptyEdges = new HashSet<>();

    public FirstFaNode() {
        this(null);
    }

    public FirstFaNode(Lexem lexem) {
        this.lexem = lexem;
        index = allStates.size();
        allStates.add(this);
    }

    public List<Edge1> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Set<FirstFaNode> getEmptyEdges() {
        return Collections.unmodifiableSet(emptyEdges);
    }

    public void addEmptyEdge(FirstFaNode to) {
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

    public void addEdge(CharacterSet characterSet, FirstFaNode to) {
        edges.add(new Edge1(characterSet, to));
    }
}
