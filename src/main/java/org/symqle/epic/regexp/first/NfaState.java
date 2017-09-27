package org.symqle.epic.regexp.first;

import org.symqle.epic.regexp.Edge;
import org.symqle.epic.regexp.Lexem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aizyurov on 9/27/17.
 */
public class NfaState {
    protected final List<Edge> edges = new ArrayList<>();
    protected Set<FirstFaState> emptyEdges = new HashSet<>();

    public void addEdge(CharacterSet characterSet, FirstFaState to) {
        edges.add(new Edge(characterSet, to));
    }
}
