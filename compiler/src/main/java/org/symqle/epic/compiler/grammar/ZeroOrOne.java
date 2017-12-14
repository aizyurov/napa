package org.symqle.epic.compiler.grammar;

import org.symqle.epic.gparser.RuleItem;
import org.symqle.epic.gparser.ZeroOrOneItem;

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
