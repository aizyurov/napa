package org.symqle.napa.gparser;

import org.symqle.napa.parser.NapaRuleItem;
import org.symqle.napa.parser.NapaTerminalItem;
import org.symqle.napa.parser.RuleItemType;

import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class TerminalItem implements RuleItem {

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

    public String toString(Vocabulary grammar) {
        return grammar.getTerminalName(value);
    }


    @Override
    public NapaRuleItem toNapaRuleItem(final Vocabulary grammar) {
        return new NapaTerminalItem(value, grammar.getTerminalName(value));
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