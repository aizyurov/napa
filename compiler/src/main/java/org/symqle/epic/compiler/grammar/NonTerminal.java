package org.symqle.epic.compiler.grammar;

import org.symqle.epic.gparser.NonTerminalItem;
import org.symqle.epic.gparser.RuleItem;

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
