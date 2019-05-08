package org.symqle.napa.compiler.grammar;

import org.symqle.napa.parser.NapaRuleItem;
import org.symqle.napa.parser.NapaZeroOrOneItem;
import org.symqle.napa.parser.RuleItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ZeroOrOneItem implements RuleItem {

    private final List<List<RuleItem>> options;

    public ZeroOrOneItem(final List<List<RuleItem>> options) {
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
            expansion.add(Collections.unmodifiableList(option)); // one
        }
        return expansion;
    }

    @Override
    public String toString(Vocabulary grammar) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < options.size(); i++) {
            if (i != 0) {
                builder.append(" |");
            }
            for (RuleItem item: options.get(i)) {
                builder.append(" ").append(item.toString(grammar));
            }
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public NapaRuleItem toNapaRuleItem(final Vocabulary grammar) {
        List<List<NapaRuleItem>> napaOptions = new ArrayList<>();
        for (List<RuleItem> items: options) {
            napaOptions.add(items.stream().map(x -> x.toNapaRuleItem(grammar)).collect(Collectors.toList()));
        }
        return new NapaZeroOrOneItem(napaOptions, grammar.hasEmptyDerivation(this), grammar.getFirstSet(this));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ZeroOrOneItem that = (ZeroOrOneItem) o;

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
