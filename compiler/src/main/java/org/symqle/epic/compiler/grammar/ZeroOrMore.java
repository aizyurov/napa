package org.symqle.epic.compiler.grammar;

import org.symqle.epic.gparser.RuleItem;
import org.symqle.epic.gparser.ZeroOrMoreItem;

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
