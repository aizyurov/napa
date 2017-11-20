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

    private final CompiledGrammar grammar;

    public NapaNonTerminalItem(final int value, CompiledGrammar grammar) {
        this.value = value;
        this.grammar = grammar;
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
    public boolean hasEmptyDerivation() {
        return grammar.hasEmptyDerivation(value);
    }

    @Override
    public Set<Integer> first() {
        return grammar.getFirstSet(value);
    }

    @Override
    public String toString() {
        return grammar.getNonTerminalName(value);
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead) {
        if (hasEmptyDerivation() || lookAhead != null && lookAhead.getType().matches(first())) {
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
