package org.symqle.epic.analyser.grammar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class GaGrammar {

    private final List<IgnoreStatement> ignored = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final Dictionary dictionary = new Dictionary();

    public void addIgnore(IgnoreStatement ignoreStatement) {
        ignored.add(ignoreStatement);
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }
}
