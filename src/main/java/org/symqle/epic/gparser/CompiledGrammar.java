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

    private final String[] nonTerminals;
    private final String[] terminals;
    private final Map<Integer, List<CompiledRule>> rules;
    private final PackedDfa<Set<Integer>> tokenizerDfa;

    public CompiledGrammar(final String[] nonTerminals, final String[] terminals, final Map<Integer, List<CompiledRule>> rules, final PackedDfa<Set<Integer>> tokenizerDfa) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules;
        this.tokenizerDfa = tokenizerDfa;
        verify();
    }

    private void verify() {
        for (int i = 0; i < nonTerminals.length; i++) {
            if (!rules.containsKey(i)) {
                throw new GrammarException("No rule for " + nonTerminals[i]);
            }
        }
    }



    public List<CompiledRule> getRules(int index) {
        return Collections.unmodifiableList(rules.get(index));
    }

    public String getTerminalName(int index) {
        return terminals[index];
    }

    public String getNonTerminalName(int index) {
        return nonTerminals[index];
    }

    public Optional<Integer> findNonTerminalByName(String name) {
        for (int i = 0; i < nonTerminals.length; i++ ) {
            if (nonTerminals[i].equals(name)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public PackedDfa<Set<Integer>> getTokenizerDfa() {
        return tokenizerDfa;
    }
}
