package org.symqle.napa.compiler.grammar;

import org.symqle.napa.gparser.NonTerminalItem;
import org.symqle.napa.gparser.RuleItem;

/**
 * @author lvovich
 */
public class NonTerminal implements RuleItemsSupplier {

    private final String name;

    public NonTerminal(final String name) {
        this.name = name;
    }

    @Override
    public RuleItem toRuleItem(final Dictionary dictionary) {
        return new NonTerminalItem(dictionary.registerNonTerminal(name));
    }
}
