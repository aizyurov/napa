package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.Edge1;
import org.symqle.epic.regexp.Edge2;
import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.first.FirstFaNode;

import java.util.*;

/**
 * Created by aizyurov on 9/27/17.
 */
public class SecondStep {

    public Collection<SecondFaNode> convert(FirstFaNode startState) {
        Map<FirstFaNode, Set<FirstFaNode>> closureMap = new LinkedHashMap<>();
        Set<FirstFaNode> queue = new HashSet<>();
        queue.add(startState);
        while (!queue.isEmpty()) {
            final FirstFaNode next = queue.iterator().next();
            for (FirstFaNode emptyEdge: next.getEmptyEdges()) {
                if (!closureMap.keySet().contains(emptyEdge)) {
                    queue.add(emptyEdge);
                }
            }
            for (Edge1 edge: next.getEdges()) {
                final FirstFaNode to = edge.getTo();
                if (!closureMap.keySet().contains(to)) {
                    queue.add(to);
                }
            }
            closureMap.put(next, closure(next));
            queue.remove(next);
        }

        Map<Set<FirstFaNode>, SecondFaNode> stateMap = new LinkedHashMap<>();
        for (FirstFaNode first: closureMap.keySet()) {
            final Set<FirstFaNode> stateSet = closureMap.get(first);
            if (!stateMap.containsKey(stateSet)) {
                final List<Lexem> lexems = new ArrayList<>();
                for (FirstFaNode state: stateSet) {
                    final Lexem lexem = state.getLexem();
                    if (lexem != null) {
                        lexems.add(lexem);
                    }
                }
                SecondFaNode second = new SecondFaNode(lexems);
                stateMap.put(closureMap.get(first), second);
            }
        }
        for (Set<FirstFaNode> stateSet: stateMap.keySet()) {
            final SecondFaNode second = stateMap.get(stateSet);
            for (FirstFaNode state: stateSet) {
                for (Edge1 edge: state.getEdges()) {
                    Set<FirstFaNode> toNodeSet = closureMap.get(edge.getTo());
                    second.addEdge(new Edge2(edge.getCharacterSet(), stateMap.get(toNodeSet)));
                }
            }
        }
        return stateMap.values();
    }

    private Set<FirstFaNode> closure(FirstFaNode origin) {
        Set<FirstFaNode> processed = new LinkedHashSet<>();
        Set<FirstFaNode> queue = new HashSet<>();
        queue.add(origin);
        while (!queue.isEmpty()) {
            final FirstFaNode next = queue.iterator().next();
            for (FirstFaNode emptyEdge: next.getEmptyEdges()) {
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
