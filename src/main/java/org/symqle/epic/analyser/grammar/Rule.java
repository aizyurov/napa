package org.symqle.epic.analyser.grammar;

import org.symqle.epic.gparser.CompiledRule;

import java.util.Collections;

/**
 * @author lvovich
 */
public class Rule {

    private final String target;
    private final Choice choice;

    public Rule(final String target, final Choice choice) {
        this.target = target;
        this.choice = choice;
    }

    public CompiledRule toCompiledRule(Dictionary dictionary) {
        return new CompiledRule(dictionary.registerNonTerminal(target), Collections.singletonList(choice.toRuleItem(dictionary)));
    }
}
