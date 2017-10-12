package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.PackedDfa;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author lvovich
 */
public class CompiledGrammar {

    final List<String> nonTerminals;
    final List<String> terminals;
    final Map<Integer, List<CompiledRule>> rules;
    final PackedDfa<Set<Integer>> tokenizerDfa;

    public CompiledGrammar(final List<String> nonTerminals, final List<String> terminals, final Map<Integer, List<CompiledRule>> rules, final PackedDfa<Set<Integer>> tokenizerDfa) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
        this.tokenizerDfa = tokenizerDfa;
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
