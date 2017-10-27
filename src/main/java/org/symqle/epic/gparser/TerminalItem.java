package org.symqle.epic.gparser;

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
        throw new UnsupportedOperationException("Not applicable");
    }

    public String toString(CompiledGrammar grammar) {
        return grammar.getTerminalName(value);
    }
}
