package org.symqle.epic.regexp.first;


import org.symqle.epic.regexp.Edge;
import org.symqle.epic.regexp.Lexem;

import java.util.*;

/**
 * Created by aizyurov on 9/27/17.
 */
public class FirstFaState extends NfaState {

    private static List<FirstFaState> allStates = new ArrayList<>();
    protected final Lexem lexem;

    private final int index;

    public FirstFaState() {
        this(null);
    }

    public FirstFaState(Lexem lexem) {
        this.lexem = lexem;
        index = allStates.size();
        allStates.add(this);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public Set<FirstFaState> getEmptyEdges() {
        return Collections.unmodifiableSet(emptyEdges);
    }

    public void addEmptyEdge(FirstFaState to) {
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
}
