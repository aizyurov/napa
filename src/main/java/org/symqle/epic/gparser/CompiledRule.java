package org.symqle.epic.gparser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public NapaRule toNapaRule(CompiledGrammar grammar, Map<RuleItem, NapaRuleItem> cache) {
        return new NapaRule(target, items.stream().map(x -> x.toNapaRuleItem(grammar, cache)).collect(Collectors.toList()));
    }
}
