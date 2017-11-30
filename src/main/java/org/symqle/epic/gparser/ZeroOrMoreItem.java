package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ZeroOrMoreItem extends AbstractRuleItem {

    private final List<List<RuleItem>> options;

    public ZeroOrMoreItem(final List<List<RuleItem>> options) {
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
        expansion.add(Collections.emptyList()); // zero
        for (List<RuleItem> option: options) {
            ArrayList<RuleItem> sequence = new ArrayList<>(option);
            sequence.add(this);
            expansion.add(sequence);
        }
        return expansion;
    }

    @Override
    public String toString(CompiledGrammar grammar) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < options.size(); i++) {
            if (i != 0) {
                builder.append(" |");
            }
            for (RuleItem item: options.get(i)) {
                builder.append(" ").append(item.toString(grammar));
            }
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    protected NapaRuleItem createNapaRuleItem(final CompiledGrammar grammar, final Map<RuleItem, NapaRuleItem> cache) {
        List<List<NapaRuleItem>> napaOptions = new ArrayList<>();
        for (List<RuleItem> items: options) {
            napaOptions.add(items.stream().map(x -> x.toNapaRuleItem(grammar, cache)).collect(Collectors.toList()));
        }
        return new NapaZeroOrMoreItem(napaOptions, grammar.hasEmptyDerivation(this), grammar.getFirstSet(this));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ZeroOrMoreItem that = (ZeroOrMoreItem) o;

        return options.equals(that.options);

    }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            hash = h = options.hashCode();
        }
        return h;
    }

    private int hash;

}
