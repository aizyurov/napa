package org.symqle.epic.gparser;

import org.symqle.epic.parser.NapaRuleItem;
import org.symqle.epic.parser.RuleItemType;

import java.util.List;

/**
 * @author lvovich
 */
public interface RuleItem {
    RuleItemType getType();

    int getValue();

    List<List<RuleItem>> expand();


    String toString(Vocabulary grammar);

    NapaRuleItem toNapaRuleItem(Vocabulary grammar);

}
