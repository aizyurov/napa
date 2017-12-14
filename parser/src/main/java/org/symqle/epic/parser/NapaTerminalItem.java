package org.symqle.epic.parser;

import org.symqle.epic.tokenizer.Token;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class NapaTerminalItem implements NapaRuleItem {

    private final int value;
    private final String name;

    public NapaTerminalItem(final int value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public RuleItemType getType() {
        return RuleItemType.TERMINAL;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean hasEmptyDerivation() {
        return false;
    }

    @Override
    public Set<Integer> first() {
        return Collections.singleton(value);
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead, final CompiledGrammar grammar) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NapaTerminalItem that = (NapaTerminalItem) o;

        if (value != that.value) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
