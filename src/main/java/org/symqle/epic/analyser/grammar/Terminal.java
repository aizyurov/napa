package org.symqle.epic.analyser.grammar;

import org.symqle.epic.gparser.RuleItem;
import org.symqle.epic.gparser.TerminalItem;

/**
 * @author lvovich
 */
public class Terminal implements RuleItemsSupplier {

    private final Regexp regexp;

    public Terminal(final Regexp regexp) {
        this.regexp = regexp;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return new TerminalItem(dictionary.registerRegexp(regexp.getValue()));
    }
}
