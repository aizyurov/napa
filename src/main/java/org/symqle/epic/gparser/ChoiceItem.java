package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ChoiceItem implements RuleItem {

    private final List<List<RuleItem>> options;

    public ChoiceItem(final List<List<RuleItem>> options) {
        this.options = options;
    }

    @Override
    public RuleItemType getType() {
        return RuleItemType.COMPOUND;
    }

    @Override
    public int getValue() {
        throw new UnsupportedOperationException("Not applicable");
    }

    @Override
    public List<List<RuleItem>> expand() {
        List<List<RuleItem>> expansion = new ArrayList<>();
        for (List<RuleItem> option: options) {
            expansion.add(Collections.unmodifiableList(option));
        }
        return expansion;
    }

    @Override
    public String toString(CompiledGrammar grammar) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < options.size(); i++) {
            if (i != 0) {
                builder.append(" |");
            }
            for (RuleItem item: options.get(i)) {
                builder.append(" ").append(item.toString(grammar));
            }
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public NapaRuleItem toNapaRuleItem(final CompiledGrammar grammar) {
        List<List<NapaRuleItem>> napaOptions = new ArrayList<>();
        for (List<RuleItem> items: options) {
            napaOptions.add(items.stream().map(x -> x.toNapaRuleItem(grammar)).collect(Collectors.toList()));
        }
        return new NapaChoiceItem(napaOptions);
    }
}
