package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.RuleItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class Chain {

    private final List<RuleItemsSupplier> items;

    public Chain(final List<RuleItemsSupplier> items) {
        this.items = items;
    }

    public List<RuleItem> toRuleItems(final Dictionary dictionary) {
        return items.stream().map(item -> item.toRuleItem(dictionary)).collect(Collectors.toList());
    }
}
