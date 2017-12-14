package org.symqle.epic.lexer.build;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
class Nfa2 {

    private final Collection<NfaNode2> nodes;

    public Nfa2(final Collection<NfaNode2> nodes) {
        this.nodes = nodes;
    }

    public Dfa toDfa() {

        Set<CharacterSet> allCharacterSets = nodes.stream().flatMap(x -> x.getEdges().stream()).map(NfaNode2.Edge::getCharacterSet).collect(Collectors.toSet());
        CharacterClassRegistry registry = new CharacterClassRegistry(allCharacterSets);

        Map<Set<NfaNode2>, DfaNode> dfaNodeMap = new LinkedHashMap<>();
        Set<Set<NfaNode2>> processed = new LinkedHashSet<>();
        Set<Set<NfaNode2>> queue = new HashSet<>();


        Set<NfaNode2> startSet = Collections.singleton(nodes.iterator().next());
        queue.add(startSet);
        DfaNode startDfa = new DfaNode(collectTags(startSet));
        dfaNodeMap.put(startSet, startDfa);

        while (!queue.isEmpty()) {
            Set<NfaNode2> nfaSet = queue.iterator().next();
            queue.remove(nfaSet);
            processed.add(nfaSet);
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
                DfaNode dfaNode = dfaNodeMap.getOrDefault(nfaNodeSet, new DfaNode(collectTags(nfaNodeSet)));
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
        return new Dfa(new ArrayList<>(dfaNodeMap.values()), registry);
    }

    private Set<Integer> collectTags(final Set<NfaNode2> nfaNodeSet) {
        return nfaNodeSet.stream()
                .flatMap(n -> n.getTags().stream())
                .collect(Collectors.toSet());
    }

}
