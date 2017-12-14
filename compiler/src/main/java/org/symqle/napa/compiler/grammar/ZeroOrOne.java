package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.RuleItem;
import org.symqle.napa.gparser.ZeroOrOneItem;

/**
 * @author lvovich
 */
public class ZeroOrOne implements RuleItemsSupplier {

    private final Choice choice;

    public ZeroOrOne(final Choice choice) {
        this.choice = choice;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return new ZeroOrOneItem(choice.toRuleItemLists(dictionary));
    }
}
