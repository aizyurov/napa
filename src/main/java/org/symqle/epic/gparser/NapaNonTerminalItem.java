package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class NapaNonTerminalItem implements NapaRuleItem {

    private final int value;
    private final String name;


    private boolean hasEmptyDerivation;
    private Set<Integer> first;

    public NapaNonTerminalItem(final int value, String name, boolean hasEmptyDerivation, Set<Integer> first) {
        this.value = value;
        this.name = name;
        this.hasEmptyDerivation = hasEmptyDerivation;
        this.first = first;
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
    public Set<Integer> first() {
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
        if (hasEmptyDerivation() || lookAhead.getType() != null && lookAhead.getType().matches(first())) {
            return grammar.getNapaRules(value).stream().map(NapaRule::getItems).collect(Collectors.toList());
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
