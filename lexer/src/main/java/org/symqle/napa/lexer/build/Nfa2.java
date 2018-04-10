package org.symqle.napa.lexer.build;

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

    public Nfa3 toNfa3() {
        Map<NfaNode2, NfaNode3> nodeMap = nodes.stream().collect(Collectors.toMap(x -> x, x -> new NfaNode3(x.getTags()), (a,b) -> a, LinkedHashMap::new));
        for (NfaNode2 node2: nodes) {
            NfaNode3 node3 = nodeMap.get(node2);
            for (NfaNode2.Edge edge: node2.getEdges()) {
                NfaNode3 target = nodeMap.get(edge.getTo());
                node3.addEdge(target, edge.getCharacterSet());
            }
        }
        return new Nfa3(nodeMap.values());
    }


}
