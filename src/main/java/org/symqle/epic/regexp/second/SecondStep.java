package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.Edge;
import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.first.FirstFaState;
import org.symqle.epic.regexp.model.CharSet;

import java.util.*;

/**
 * Created by aizyurov on 9/27/17.
 */
public class SecondStep {

    public SecondFaState convert(FirstFaState startState) {
        Map<FirstFaState, Set<FirstFaState>> closureMap = new HashMap<>();
        Set<FirstFaState> queue = new HashSet<>();
        queue.add(startState);
        while (!queue.isEmpty()) {
            final FirstFaState next = queue.iterator().next();
            for (FirstFaState emptyEdge: next.getEmptyEdges()) {
                if (!closureMap.keySet().contains(emptyEdge)) {
                    queue.add(emptyEdge);
                }
            }
            for (Edge edge: next.getEdges()) {
                final FirstFaState to = edge.getTo();
                if (!closureMap.keySet().contains(to)) {
                    queue.add(to);
                }
            }
            closureMap.put(next, closure(next));
            queue.remove(next);
        }

        Map<Set<FirstFaState>, SecondFaState> stateMap = new HashMap<>();
        for (FirstFaState first: closureMap.keySet()) {
            final Set<FirstFaState> stateSet = closureMap.get(first);
            if (!stateMap.containsKey(stateSet)) {
                final List<Lexem> lexems = new ArrayList<>();
                for (FirstFaState state: stateSet) {
                    final Lexem lexem = state.getLexem();
                    if (lexem != null) {
                        lexems.add(lexem);
                    }
                }
                SecondFaState second = new SecondFaState(lexems);
                stateMap.put(closureMap.get(first), second);
            }
        }
        for (Set<FirstFaState> stateSet: stateMap.keySet()) {
            final SecondFaState second = stateMap.get(stateSet);
            for (FirstFaState state: stateSet) {
                for (Edge edge: state.getEdges()) {
                    second.addEdge(edge);
                }
            }
        }
        return stateMap.get(closureMap.get(startState));
    }

    private Set<FirstFaState> closure(FirstFaState origin) {
        Set<FirstFaState> processed = new HashSet<>();
        Set<FirstFaState> queue = new HashSet<>();
        queue.add(origin);
        while (!queue.isEmpty()) {
            final FirstFaState next = queue.iterator().next();
            for (FirstFaState emptyEdge: next.getEmptyEdges()) {
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
