package org.symqle.epic.analyser.grammar;

import org.symqle.epic.gparser.CompiledRule;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<CompiledRule> toCompiledRules(Dictionary dictionary) {
        Integer targetTag = dictionary.registerNonTerminal(this.target);
        return choice.toRuleItemLists(dictionary).stream()
                .map(items -> new CompiledRule(targetTag, items))
                .collect(Collectors.toList());
    }
}
