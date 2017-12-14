package org.symqle.epic.compiler.grammar;

import org.symqle.epic.gparser.RuleItem;

/**
 * @author lvovich
 */
public interface RuleItemsSupplier {

    RuleItem toRuleItem(Dictionary dictionary);
}
