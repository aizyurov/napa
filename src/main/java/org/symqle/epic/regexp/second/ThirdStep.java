package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.first.CharacterSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ThirdStep {

    Map<Set<NfaNode2>, DfaNode> dfaNodeMap = new LinkedHashMap<>();
    CharacterClassRegistry registry;

    public DfaNode build(Collection<NfaNode2> secondNfa) {
        Set<CharacterSet> allCharacterSets = secondNfa.stream().flatMap(x -> x.getEdges().stream()).map(NfaNode2.Edge::getCharacterSet).collect(Collectors.toSet());
        registry = new CharacterClassRegistry(allCharacterSets);


        Set<Set<NfaNode2>> processed = new LinkedHashSet<>();
        Set<Set<NfaNode2>> queue = new HashSet<>();

        Set<NfaNode2> startSet = Collections.singleton(secondNfa.iterator().next());
        queue.add(startSet);
        DfaNode startDfa = new DfaNode(extractLabels(startSet));
        dfaNodeMap.put(startSet, startDfa);

        Set<NfaNode2> previousNfaSet = null;
        while (!queue.isEmpty()) {
            Set<NfaNode2> nfaSet = queue.iterator().next();
            queue.remove(nfaSet);
            processed.add(nfaSet);
            if (queue.contains(nfaSet)) {
                throw new IllegalStateException(("element not removed"));
            }
            Map<Integer, Set<NfaNode2>> nodeSetsByCharacterClass = new HashMap<>();
            for (NfaNode2 nfaNode : nfaSet) {
                for (NfaNode2.Edge edge : nfaNode.getEdges()) {
                    Set<Integer> characterClasses = registry.getCharacterClasses(edge.getCharacterSet());
                    for (Integer characterClass : characterClasses) {
                        Set<NfaNode2> nodeSet = nodeSetsByCharacterClass.getOrDefault(characterClass, new HashSet<>());
                        nodeSet.add(edge.getTo());
                        nodeSetsByCharacterClass.put(characterClass, nodeSet);
                    }
                }
            }
            for (Set<NfaNode2> nfaNodeSet : nodeSetsByCharacterClass.values()) {
                DfaNode dfaNode = dfaNodeMap.getOrDefault(nfaNodeSet, new DfaNode(extractLabels(nfaNodeSet)));
                dfaNodeMap.put(nfaNodeSet, dfaNode);
                if (!processed.contains(nfaNodeSet)) {
                    queue.add(new HashSet<>(nfaNodeSet));
                }
            }
            DfaNode currentDfaNode = dfaNodeMap.get(nfaSet);
            for (Map.Entry<Integer, Set<NfaNode2>> entry: nodeSetsByCharacterClass.entrySet()) {
                currentDfaNode.addEdge(entry.getKey(), dfaNodeMap.get(entry.getValue()));
            }
        }
        return startDfa;
    }

    private Set<String> extractLabels(final Set<NfaNode2> nfaNodeSet) {
        Map<Boolean, Set<String>> labelsMap = nfaNodeSet.stream()
                .flatMap(n -> n.getLexems().stream())
                .collect(Collectors.partitioningBy(Lexem::isMeaningiful, Collectors.mapping(Lexem::getPattern, Collectors.toSet())));
        // try first meaningful lexems, then ignored
        return labelsMap.getOrDefault(true, labelsMap.getOrDefault(false, Collections.emptySet()));
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
