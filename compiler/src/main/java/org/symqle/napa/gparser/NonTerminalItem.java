package org.symqle.napa.gparser;

import org.symqle.napa.parser.NapaNonTerminalItem;
import org.symqle.napa.parser.NapaRuleItem;
import org.symqle.napa.parser.RuleItemType;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class NonTerminalItem implements RuleItem  {

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
        return Collections.emptyList();
    }

    public String toString(Vocabulary grammar) {
        return grammar.getNonTerminalName(value);
    }



    @Override
    public NapaRuleItem toNapaRuleItem(final Vocabulary grammar) {
        boolean hasEmptyDerivation = grammar.hasEmptyDerivation(this);
        Set<Integer> firstSet = grammar.getFirstSet(this);
        return new NapaNonTerminalItem(value, grammar.getNonTerminalName(value), hasEmptyDerivation, firstSet);
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
