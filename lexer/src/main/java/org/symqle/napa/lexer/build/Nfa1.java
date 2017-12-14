package org.symqle.napa.lexer.build;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
class Nfa1 {

    private final NfaNode1 startNode;

    private final Map<NfaNode1, Set<NfaNode1>> closureMap = new LinkedHashMap<>();

    private final Map<Set<NfaNode1>, NfaNode2> stateMap = new LinkedHashMap<>();

    public Nfa1(final NfaNode1 startNode) {
        this.startNode = startNode;
    }

    public Nfa2 removeEmptyEdges() {

        findClosures();
        createNfa2Nodes();
        createNfa2Edges();
        return new Nfa2(stateMap.values());
    }

    private void createNfa2Edges() {
        for (Set<NfaNode1> stateSet: stateMap.keySet()) {
            final NfaNode2 second = stateMap.get(stateSet);
            for (NfaNode1 state: stateSet) {
                for (NfaNode1.Edge edge: state.getEdges()) {
                    Set<NfaNode1> toNodeSet = closureMap.get(edge.getTo());
                    second.addEdge(new NfaNode2.Edge(edge.getCharacterSet(), stateMap.get(toNodeSet)));
                }
            }
        }
    }

    private void createNfa2Nodes() {
        for (NfaNode1 first: closureMap.keySet()) {
            final Set<NfaNode1> stateSet = closureMap.get(first);
            if (!stateMap.containsKey(stateSet)) {
                final Set<Integer> tags = new HashSet<>();
                for (NfaNode1 state: stateSet) {
                    final int tag = state.getTag();
                    if (tag >= 0) {
                        tags.add(tag);
                    }
                }
                NfaNode2 second = new NfaNode2(tags);
                stateMap.put(closureMap.get(first), second);
            }
        }
    }

    private void findClosures() {
        Set<NfaNode1> queue = new HashSet<>();
        queue.add(startNode);
        while (!queue.isEmpty()) {
            final NfaNode1 next = queue.iterator().next();
            for (NfaNode1 emptyEdge: next.getEmptyEdges()) {
                if (!closureMap.keySet().contains(emptyEdge)) {
                    queue.add(emptyEdge);
                }
            }
            for (NfaNode1.Edge edge: next.getEdges()) {
                final NfaNode1 to = edge.getTo();
                if (!closureMap.keySet().contains(to)) {
                    queue.add(to);
                }
            }
            closureMap.put(next, closure(next));
            queue.remove(next);
        }
    }

    private Set<NfaNode1> closure(NfaNode1 origin) {
        Set<NfaNode1> processed = new LinkedHashSet<>();
        Set<NfaNode1> queue = new HashSet<>();
        queue.add(origin);
        while (!queue.isEmpty()) {
            final NfaNode1 next = queue.iterator().next();
            for (NfaNode1 emptyEdge: next.getEmptyEdges()) {
                if (!processed.contains(emptyEdge)) {
                    queue.add(emptyEdge);
                }
            }
            processed.add(next);
            queue.remove(next);
        }
        return processed;
    }

}
