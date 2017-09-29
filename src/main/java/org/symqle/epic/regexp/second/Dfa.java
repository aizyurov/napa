package org.symqle.epic.regexp.second;

import java.util.List;

/**
 * @author lvovich
 */
public class Dfa {
    private final List<DfaNode> nodes;
    private final CharacterClassRegistry registry;

    public Dfa(final List<DfaNode> nodes, final CharacterClassRegistry registry) {
        this.nodes = nodes;
        this.registry = registry;
    }

    public List<DfaNode> getNodes() {
        return nodes;
    }

    public CharacterClassRegistry getRegistry() {
        return registry;
    }
}
