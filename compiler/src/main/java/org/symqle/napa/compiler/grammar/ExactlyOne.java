package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.RuleItem;

/**
 * @author lvovich
 */
public class ExactlyOne implements RuleItemsSupplier {

    private final Choice choice;

    public ExactlyOne(final Choice choice) {
        this.choice = choice;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return choice.toRuleItem(dictionary);
    }
}
