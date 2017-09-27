package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.Edge2;
import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.first.FirstFaNode;
import org.symqle.epic.regexp.first.NfaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aizyurov on 9/27/17.
 */
public class SecondFaNode extends NfaNode {

    private static List<SecondFaNode> allStates = new ArrayList<>();
    private final int index;
    private final List<Lexem> lexems;
    protected Set<FirstFaNode> emptyEdges = new HashSet<>();

    public SecondFaNode(List<Lexem> lexems) {
        this.lexems = lexems;
        index = allStates.size();
        allStates.add(this);
    }

    private final List<Edge2> edges = new ArrayList<>();

    public void addEdge(Edge2 edge) {
        edges.add(edge);
    }

    public List<Edge2> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Lexem> getLexems() {
        return lexems;
    }

    public static int size() {
        return allStates.size();
    }

    public static int edgeCount() {
        int count = 0;
        for (SecondFaNode state: allStates) {
            count += state.getEdges().size();
        }
        return count;
    }
}
