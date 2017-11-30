package org.symqle.epic.gparser;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class TerminalItem extends AbstractRuleItem {

    private final int value;

    public TerminalItem(final int value) {
        this.value = value;
    }

    @Override
    public RuleItemType getType() {
        return RuleItemType.TERMINAL;
    }

    public int getValue() {
        return value;
    }

    @Override
    public List<List<RuleItem>> expand() {
        return Collections.emptyList();
    }

    public String toString(CompiledGrammar grammar) {
        return grammar.getTerminalName(value);
    }


    @Override
    protected NapaRuleItem createNapaRuleItem(final CompiledGrammar grammar, final Map<RuleItem, NapaRuleItem> cache) {
        return new NapaTerminalItem(value, grammar);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TerminalItem that = (TerminalItem) o;

        return value == that.value;

    }

    @Override
    public int hashCode() {
        return value;
    }
}
