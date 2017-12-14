package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.RuleItem;
import org.symqle.napa.gparser.ZeroOrMoreItem;

/**
 * @author lvovich
 */
public class ZeroOrMore implements RuleItemsSupplier {

    private final Choice choice;

    public ZeroOrMore(final Choice choice) {
        this.choice = choice;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return new ZeroOrMoreItem(choice.toRuleItemLists(dictionary));
    }
}
