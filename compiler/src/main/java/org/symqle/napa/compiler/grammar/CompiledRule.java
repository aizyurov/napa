package org.symqle.napa.compiler.grammar;

import org.symqle.napa.parser.NapaRule;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class CompiledRule {

    final int target;
    final List<RuleItem> items;

    public CompiledRule(final int target, final List<RuleItem> items) {
        this.target = target;
        this.items = items;
    }

    public int getTarget() {
        return target;
    }

    public List<RuleItem> getItems() {
        return items;
    }

    public NapaRule toNapaRule(Vocabulary grammar) {
        return new NapaRule(target, items.stream().map(x -> x.toNapaRuleItem(grammar)).collect(Collectors.toList()));
    }
}
