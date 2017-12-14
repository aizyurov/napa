package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.RuleItem;
import org.symqle.napa.gparser.TerminalItem;

/**
 * @author lvovich
 */
public class Terminal implements RuleItemsSupplier {

    private final String regexp;

    public Terminal(final String regexp) {
        this.regexp = regexp;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return new TerminalItem(dictionary.registerRegexp(regexp));
    }
}
