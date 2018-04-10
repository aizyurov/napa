package org.symqle.napa.lexer.build;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class Nfa3 {

    private final Collection<NfaNode3> nodes;

    public Nfa3(final Collection<NfaNode3> nodes) {
        this.nodes = nodes;
    }

    public Dfa toDfa() {

        Set<CharacterSet> allCharacterSets = nodes.stream().flatMap(x -> x.getEdges().values().stream()).collect(Collectors.toSet());
        CharacterClassRegistry registry = new CharacterClassRegistry(allCharacterSets);

        Map<Set<NfaNode3>, DfaNode> dfaNodeMap = new LinkedHashMap<>();
        Set<Set<NfaNode3>> processed = new LinkedHashSet<>();
        Set<Set<NfaNode3>> queue = new HashSet<>();


        Set<NfaNode3> startSet = Collections.singleton(nodes.iterator().next());
        queue.add(startSet);
        DfaNode startDfa = new DfaNode(collectTags(startSet));
        dfaNodeMap.put(startSet, startDfa);

        while (!queue.isEmpty()) {
            Set<NfaNode3> nfaSet = queue.iterator().next();
            queue.remove(nfaSet);
            processed.add(nfaSet);
            Map<Integer, Set<NfaNode3>> nodeSetsByCharacterClass = new HashMap<>();
            for (NfaNode3 nfaNode : nfaSet) {
                for (Map.Entry<NfaNode3, CharacterSet> entry : nfaNode.getEdges().entrySet()) {
                    CharacterSet characterSet = entry.getValue();
                    NfaNode3 target = entry.getKey();
                    Set<Integer> characterClasses = registry.getCharacterClasses(characterSet);
                    for (Integer characterClass : characterClasses) {
                        Set<NfaNode3> nodeSet = nodeSetsByCharacterClass.getOrDefault(characterClass, new HashSet<>());
                        nodeSet.add(target);
                        nodeSetsByCharacterClass.put(characterClass, nodeSet);
                    }
                }
            }
            for (Set<NfaNode3> nfaNodeSet : nodeSetsByCharacterClass.values()) {
                DfaNode dfaNode = dfaNodeMap.getOrDefault(nfaNodeSet, new DfaNode(collectTags(nfaNodeSet)));
                dfaNodeMap.put(nfaNodeSet, dfaNode);
                if (!processed.contains(nfaNodeSet)) {
                    queue.add(new HashSet<>(nfaNodeSet));
                }
            }
            DfaNode currentDfaNode = dfaNodeMap.get(nfaSet);
            for (Map.Entry<Integer, Set<NfaNode3>> entry: nodeSetsByCharacterClass.entrySet()) {
                currentDfaNode.addEdge(entry.getKey(), dfaNodeMap.get(entry.getValue()));
            }
        }
        return new Dfa(new ArrayList<>(dfaNodeMap.values()), registry);
    }

    private Set<Integer> collectTags(final Set<NfaNode3> nfaNodeSet) {
        return nfaNodeSet.stream()
                .flatMap(n -> n.getTags().stream())
                .collect(Collectors.toSet());
    }



}
