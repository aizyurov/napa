package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.TokenDefinition;
import org.symqle.epic.regexp.first.NfaNode1;

import java.util.*;

/**
 * Created by aizyurov on 9/27/17.
 */
public class SecondStep {

    public Collection<NfaNode2> convert(NfaNode1 startState) {
        Map<NfaNode1, Set<NfaNode1>> closureMap = new LinkedHashMap<>();
        Set<NfaNode1> queue = new HashSet<>();
        queue.add(startState);
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

        Map<Set<NfaNode1>, NfaNode2> stateMap = new LinkedHashMap<>();
        for (NfaNode1 first: closureMap.keySet()) {
            final Set<NfaNode1> stateSet = closureMap.get(first);
            if (!stateMap.containsKey(stateSet)) {
                final List<TokenDefinition> tokenDefinitions = new ArrayList<>();
                for (NfaNode1 state: stateSet) {
                    final TokenDefinition tokenDefinition = state.getTokenDefinition();
                    if (tokenDefinition != null) {
                        tokenDefinitions.add(tokenDefinition);
                    }
                }
                NfaNode2 second = new NfaNode2(tokenDefinitions);
                stateMap.put(closureMap.get(first), second);
            }
        }
        for (Set<NfaNode1> stateSet: stateMap.keySet()) {
            final NfaNode2 second = stateMap.get(stateSet);
            for (NfaNode1 state: stateSet) {
                for (NfaNode1.Edge edge: state.getEdges()) {
                    Set<NfaNode1> toNodeSet = closureMap.get(edge.getTo());
                    second.addEdge(new NfaNode2.Edge(edge.getCharacterSet(), stateMap.get(toNodeSet)));
                }
            }
        }
        return stateMap.values();
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
