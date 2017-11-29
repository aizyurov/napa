package org.symqle.epic.gparser;

import java.util.List;

/**
 * @author lvovich
 */
public class NonTerminalItem implements RuleItem {

    private final int value;

    public NonTerminalItem(final int value) {
        this.value = value;
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
    public List<List<RuleItem>> expand() {
        throw new UnsupportedOperationException("Not applicable");
    }

    public String toString(CompiledGrammar grammar) {
        return grammar.getNonTerminalName(value);
    }

    @Override
    public NapaRuleItem toNapaRuleItem(final CompiledGrammar grammar) {
        return new NapaNonTerminalItem(value, grammar);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final NonTerminalItem that = (NonTerminalItem) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }
}
