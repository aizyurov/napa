package org.symqle.napa.gparser;

import org.symqle.napa.parser.NapaRuleItem;
import org.symqle.napa.parser.RuleItemType;

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
