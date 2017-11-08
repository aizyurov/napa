package org.symqle.epic.gparser;

import java.util.List;

/**
 * @author lvovich
 */
public interface RuleItem {
    RuleItemType getType();

    int getValue();

    List<List<RuleItem>> expand();


    String toString(CompiledGrammar grammar);

    NapaRuleItem toNapaRuleItem(CompiledGrammar grammar);
}
