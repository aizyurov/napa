package org.symqle.epic.gparser;

import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public interface RuleItem {
    RuleItemType getType();

    int getValue();

    List<List<RuleItem>> expand();


    String toString(CompiledGrammar grammar);

    NapaRuleItem toNapaRuleItem(CompiledGrammar grammar, Map<RuleItem, NapaRuleItem> cache);

}
