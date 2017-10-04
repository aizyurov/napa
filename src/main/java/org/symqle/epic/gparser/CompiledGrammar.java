package org.symqle.epic.gparser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author lvovich
 */
public class CompiledGrammar {

    final List<String> nonTerminals;
    final List<String> terminals;
    final Map<Integer, List<CompiledRule>> rules;

    public CompiledGrammar(final List<String> nonTerminals, final List<String> terminals, final Map<Integer, List<CompiledRule>> rules) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
    }

    public List<CompiledRule> getRules(int index) {
        return Collections.unmodifiableList(rules.get(index));
    }

    public String getTerminalName(int index) {
        return terminals.get(index);
    }

    public String getNonTerminalName(int index) {
        return nonTerminals.get(index);
    }

    public Optional<Integer> findNonTerminalByName(String name) {
        for (int i = 0; i < nonTerminals.size(); i++ ) {
            if (nonTerminals.get(i).equals(name)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }
}
