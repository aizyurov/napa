package org.symqle.epic.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aizyurov on 8/19/16.
 */
public class RuleSet {

    private final List<Symbol> rules;

    public RuleSet(List<Symbol> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public List<Symbol> getRules() {
        return Collections.unmodifiableList(rules);
    }
}
