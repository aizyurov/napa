package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class ZeroOrOneItem implements RuleItem {

    private final List<List<RuleItem>> options;

    public ZeroOrOneItem(final List<List<RuleItem>> options) {
        this.options = options;
    }

    @Override
    public RuleItemType getType() {
        return RuleItemType.COMPOUND;
    }

    @Override
    public int getValue() {
        throw new UnsupportedOperationException("Not applicable");
    }

    @Override
    public List<List<RuleItem>> expand() {
        List<List<RuleItem>> expansion = new ArrayList<>();
        expansion.add(Collections.emptyList()); // zero
        for (List<RuleItem> option: options) {
            expansion.add(Collections.unmodifiableList(option)); // one
        }
        return expansion;
    }

    @Override
    public String toString(CompiledGrammar grammar) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < options.size(); i++) {
            if (i != 0) {
                builder.append(" |");
            }
            for (RuleItem item: options.get(i)) {
                builder.append(" ").append(item.toString(grammar));
            }
        }
        builder.append("]");
        return builder.toString();
    }

}
