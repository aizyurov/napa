package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.Edge;
import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.first.NfaState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aizyurov on 9/27/17.
 */
public class SecondFaState extends NfaState {

    private static List<SecondFaState> allStates = new ArrayList<>();
    private final int index;
    private final List<Lexem> lexems;

    public SecondFaState(List<Lexem> lexems) {
        this.lexems = lexems;
        index = allStates.size();
        allStates.add(this);
    }

    private final List<Edge> edges = new ArrayList<>();

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getEdges() {
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
        for (SecondFaState state: allStates) {
            count += state.getEdges().size();
        }
        return count;
    }
}
