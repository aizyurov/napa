package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.Edge2;
import org.symqle.epic.regexp.first.CharacterSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ThirdStep {

    Map<Set<SecondFaNode>, DfaNode> dfaNodeMap = new LinkedHashMap<>();
    CharacterClassRegistry registry;

    public DfaNode build(Collection<SecondFaNode> secondNfa) {
        Set<CharacterSet> allCharacterSets = secondNfa.stream().flatMap(x -> x.getEdges().stream()).map(Edge2::getCharacterSet).collect(Collectors.toSet());
        registry = new CharacterClassRegistry(allCharacterSets);


        Set<Set<SecondFaNode>> processed = new LinkedHashSet<>();
        Set<Set<SecondFaNode>> queue = new HashSet<>();

        Set<SecondFaNode> startSet = Collections.singleton(secondNfa.iterator().next());
        queue.add(startSet);
        DfaNode startDfa = new DfaNode();
        dfaNodeMap.put(startSet, startDfa);

        Set<SecondFaNode> previousNfaSet = null;
        while (!queue.isEmpty()) {
            Set<SecondFaNode> nfaSet = queue.iterator().next();
            queue.remove(nfaSet);
            processed.add(nfaSet);
            if (queue.contains(nfaSet)) {
                throw new IllegalStateException(("element not removed"));
            }
            Map<Integer, Set<SecondFaNode>> nodeSetsByCharacterClass = new HashMap<>();
            for (SecondFaNode nfaNode : nfaSet) {
                for (Edge2 edge : nfaNode.getEdges()) {
                    Set<Integer> characterClasses = registry.getCharacterClasses(edge.getCharacterSet());
                    for (Integer characterClass : characterClasses) {
                        Set<SecondFaNode> nodeSet = nodeSetsByCharacterClass.getOrDefault(characterClass, new HashSet<>());
                        nodeSet.add(edge.getTo());
                        nodeSetsByCharacterClass.put(characterClass, nodeSet);
                    }
                }
            }
            for (Set<SecondFaNode> nfaNodeSet : nodeSetsByCharacterClass.values()) {
                DfaNode dfaNode = dfaNodeMap.getOrDefault(nfaNodeSet, new DfaNode());
                dfaNodeMap.put(nfaNodeSet, dfaNode);
                if (!processed.contains(nfaNodeSet)) {
                    queue.add(new HashSet<>(nfaNodeSet));
                }
            }
            DfaNode currentDfaNode = dfaNodeMap.get(nfaSet);
            for (Map.Entry<Integer, Set<SecondFaNode>> entry: nodeSetsByCharacterClass.entrySet()) {
                currentDfaNode.addEdge(entry.getKey(), dfaNodeMap.get(entry.getValue()));
            }
        }
        return startDfa;
    }

    public int size() {
        return this.dfaNodeMap.size();
    }

    public int edges() {
        int edges = 0;
        for (DfaNode node: dfaNodeMap.values()) {
            edges += node.getEdges().size();
        }
        return edges;
    }

}
