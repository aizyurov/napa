package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.Token;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class NapaTerminalItem implements NapaRuleItem {

    private final int value;
    private final String name;
    private final BitSet first;

    public NapaTerminalItem(final int value, String name) {
        this.value = value;
        this.name = name;
        first = new BitSet();
        first.set(value);
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
    public BitSet first() {
        return first;
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead, final Grammar grammar) {
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
