package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.Token;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public abstract class AbstractNapaCompoundItem implements NapaRuleItem {
    private final List<RuleItemSequence> options;
    private final BitSet first;
    private final boolean canBeEmpty;

    public AbstractNapaCompoundItem(final List<List<NapaRuleItem>> options, boolean canBeEmpty, Set<Integer> first) {
        this.options = options.stream().map(RuleItemSequence::new).collect(Collectors.toList());
        this.canBeEmpty = canBeEmpty;
        this.first = new BitSet();
        for (int i: first) {
            this.first.set(i);
        }
    }

    @Override
    public boolean hasEmptyDerivation() {
        return canBeEmpty;
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
    public String getName() {
        throw new UnsupportedOperationException("Not applicable");
    }

    @Override
    public List<List<NapaRuleItem>> expand(Token<TokenProperties> lookAhead) {
        if (lookAhead.getType() != null && !lookAhead.getType().matches(first())) {
            return Collections.emptyList();
        }
        List<List<NapaRuleItem>> expansion = new ArrayList<>();
        for (RuleItemSequence option: options) {
            if (option.canBeEmpty() || lookAhead.getType() != null && lookAhead.getType().matches(option.firstSet())) {
                expansion.add(Collections.unmodifiableList(option.getSequence()));
            }
        }
        return expansion;
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead, final Grammar grammar) {
        return Collections.emptyList();
    }

    @Override
    public BitSet first() {
        return first;
    }

    protected final String optionsToString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            if (i != 0) {
                builder.append(" |");
            }
            for (NapaRuleItem item: options.get(i).getSequence()) {
                builder.append(" ").append(item.toString());
            }
        }
        builder.append(" ");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractNapaCompoundItem that = (AbstractNapaCompoundItem) o;

        return options.equals(that.options);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = options.hashCode();
        }
        return hash;

    }

    private int hash;

}
