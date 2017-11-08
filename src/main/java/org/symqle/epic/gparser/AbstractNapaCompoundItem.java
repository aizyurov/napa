package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public abstract class AbstractNapaCompoundItem implements NapaRuleItem {
    private final List<List<NapaRuleItem>> options;
    private final Set<Integer> first;

    public AbstractNapaCompoundItem(final List<List<NapaRuleItem>> options) {
        this.options = options;
        first = new HashSet<>();
        for (List<NapaRuleItem> option: options) {
            for (int i = 0; i < option.size(); i ++) {
                first.addAll(calculateFirst(option));
            }
        }
    }


    private Set<Integer> calculateFirst(List<NapaRuleItem> itemList) {
        Set<Integer> firstSet = new HashSet<>();
        for (int i = 0; i < itemList.size(); i++) {
            NapaRuleItem item = itemList.get(i);
            firstSet.addAll(item.first());
            if (!item.hasEmptyDerivation()) {
                return  firstSet;
            }
        }
        return firstSet;
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
        if (lookAhead != null && !lookAhead.getType().matches(first)) {
            return Collections.emptyList();
        }
        List<List<NapaRuleItem>> expansion = new ArrayList<>();
        for (List<NapaRuleItem> option: options) {
            expansion.add(Collections.unmodifiableList(option));
        }
        return expansion;
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public Set<Integer> first() {
        return first;
    }

    protected final String optionsToString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            if (i != 0) {
                builder.append(" |");
            }
            for (NapaRuleItem item: options.get(i)) {
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

        if (!options.equals(that.options)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return options.hashCode();
    }
}
