package org.symqle.epic.gparser;

import java.util.List;

/**
 * @author lvovich
 */
public class CompiledRule {

    final int target;
    final List<RuleItem> items;

    public CompiledRule(final int target, final List<RuleItem> items) {
        this.target = target;
        this.items = items;
    }

    public int getTarget() {
        return target;
    }

    public List<RuleItem> getItems() {
        return items;
    }
}
