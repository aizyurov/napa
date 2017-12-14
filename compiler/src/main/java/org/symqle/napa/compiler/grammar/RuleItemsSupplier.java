package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.RuleItem;

/**
 * @author lvovich
 */
public interface RuleItemsSupplier {

    RuleItem toRuleItem(Dictionary dictionary);
}
