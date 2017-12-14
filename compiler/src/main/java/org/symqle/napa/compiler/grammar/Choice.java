package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.ChoiceItem;
import org.symqle.napa.gparser.RuleItem;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class Choice implements RuleItemsSupplier {

    private final List<Chain> options;

    public Choice(final List<Chain> options) {
        this.options = options;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return new ChoiceItem(toRuleItemLists(dictionary));
    }

    public List<List<RuleItem>> toRuleItemLists(final Dictionary dictionary) {
        return options.stream().map(c -> c.toRuleItems(dictionary)).collect(Collectors.toList());
    }
}
