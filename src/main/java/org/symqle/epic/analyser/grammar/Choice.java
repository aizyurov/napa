package org.symqle.epic.analyser.grammar;

import org.symqle.epic.gparser.RuleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class Choice implements RuleItemsSupplier {

    private List<Chain> options = new ArrayList<>();

    public void addOption(Chain chain) {
        options.add(chain);
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        switch (options.size()) {
            case 0:
                throw new IllegalStateException("Empty sequence not allowed");
            case 1:
                // optimization
                return options.get(0).toRuleItem(dictionary);
            default:
                throw new UnsupportedOperationException("not implemented");
        }
    }
}
