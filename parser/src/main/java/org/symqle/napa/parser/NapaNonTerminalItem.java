package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class NapaNonTerminalItem implements NapaRuleItem {

    private final int value;
    private final String name;


    private boolean hasEmptyDerivation;
    private BitSet first;

    public NapaNonTerminalItem(final int value, String name, boolean hasEmptyDerivation, Set<Integer> first) {
        this.value = value;
        this.name = name;
        this.hasEmptyDerivation = hasEmptyDerivation;
        this.first = new BitSet();
        for (int i: first) {
            this.first.set(i);
        }
    }

    @Override
    public RuleItemType getType() {
        return RuleItemType.NON_TERMINAL;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasEmptyDerivation() {
        return hasEmptyDerivation;
    }

    @Override
    public BitSet first() {
        return first;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead, final CompiledGrammar grammar) {
        if (lookAhead.getType() != null && lookAhead.getType().matches(first) || hasEmptyDerivation) {
            final List<NapaRule> napaRules = grammar.getNapaRules(value);
            final List<List<NapaRuleItem>> result = new ArrayList<>(napaRules.size());
            for (int i=0; i < napaRules.size(); i++) {
                NapaRule rule = napaRules.get(i);
                result.add(rule.getItems());
         }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NapaNonTerminalItem that = (NapaNonTerminalItem) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }
}
