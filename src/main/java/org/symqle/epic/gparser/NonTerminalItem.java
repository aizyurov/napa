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
}
