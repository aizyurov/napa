package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class ChoiceItem implements RuleItem {

    private final List<List<RuleItem>> options;

    public ChoiceItem(final List<List<RuleItem>> options) {
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
        for (List<RuleItem> option: options) {
            expansion.add(Collections.unmodifiableList(option));
        }
        return expansion;
    }
}
