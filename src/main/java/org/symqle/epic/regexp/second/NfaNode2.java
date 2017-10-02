package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.TokenDefinition;
import org.symqle.epic.regexp.first.CharacterSet;
import org.symqle.epic.regexp.first.NfaNode1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aizyurov on 9/27/17.
 */
public class NfaNode2 {

    private static List<NfaNode2> allStates = new ArrayList<>();
    private final int index;
    private final List<TokenDefinition> tokenDefinitions;
    protected Set<NfaNode1> emptyEdges = new HashSet<>();

    public NfaNode2(List<TokenDefinition> tokenDefinitions) {
        this.tokenDefinitions = tokenDefinitions;
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

    public List<TokenDefinition> getTokenDefinitions() {
        return tokenDefinitions;
    }

    public static int size() {
        return allStates.size();
    }

    public static int edgeCount() {
        int count = 0;
        for (NfaNode2 state: allStates) {
            count += state.getEdges().size();
        }
        return count;
    }

    /**
    * Created by aizyurov on 9/27/17.
    */
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
