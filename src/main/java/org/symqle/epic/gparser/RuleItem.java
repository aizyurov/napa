package org.symqle.epic.gparser;

import java.util.List;

/**
 * @author lvovich
 */
public class RuleItem {

    public enum Type {
        TERMINAL,
        NON_TERMINAL,
        ZERO_OR_ONE,
        ZERO_OR_MORE
    }

    private final Type type;
    private final int value;
    private final List<RuleItem> items;

    public RuleItem(final Type type, final int value, final List<RuleItem> items) {
        this.type = type;
        this.value = value;
        this.items = items;
    }

    public Type getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public List<RuleItem> getItems() {
        return items;
    }
}
