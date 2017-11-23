package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public abstract class AbstractNapaCompoundItem implements NapaRuleItem {
    private final List<RuleItemSequence> options;
    private Set<Integer> first;
    private Boolean canBeEmpty;

    public AbstractNapaCompoundItem(final List<List<NapaRuleItem>> options) {
        this.options = options.stream().map(RuleItemSequence::new).collect(Collectors.toList());
    }


    private Set<Integer> calculateFirst() {
        return options.stream().flatMap(s -> s.firstSet().stream()).collect(Collectors.toSet());
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
    public List<List<NapaRuleItem>> expand(Token<TokenProperties> lookAhead) {
        if (lookAhead != null && !lookAhead.getType().matches(first())) {
            return Collections.emptyList();
        }
        List<List<NapaRuleItem>> expansion = new ArrayList<>();
        for (RuleItemSequence option: options) {
            if (option.canBeEmpty() || lookAhead != null && lookAhead.getType().matches(option.firstSet())) {
                expansion.add(Collections.unmodifiableList(option.getSequence()));
            }
        }
        return expansion;
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public Set<Integer> first() {
        if (first == null) {
            first = calculateFirst();
        }
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

    protected List<RuleItemSequence> getOptions() {
        return options;
    }
}
