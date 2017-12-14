package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class RuleInProgress0 {

    private final int target;
    private final List<RuleItem> items;
    private final int offset;
    private final Map<Integer, List<CompiledRule>> rules;

    public RuleInProgress0(final int target, final List<RuleItem> items, int offset, final Map<Integer, List<CompiledRule>> rules) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.rules = rules;
    }

    public enum Action {
        PREDICT,
        REDUCE,
        EXPAND,
        SHIFT,
        ACCEPT
    }

    public RuleInProgress0.Action availableAction() {
        if (offset < items.size()) {
            final RuleItem item = items.get(offset);
            switch (item.getType()) {
                case NON_TERMINAL:
                        return RuleInProgress0.Action.PREDICT;
                case TERMINAL:
                    return RuleInProgress0.Action.SHIFT;
                case COMPOUND:
                    return RuleInProgress0.Action.EXPAND;
                default:
                    throw new IllegalStateException("Should never get here");
            }
        } else {
            return RuleInProgress0.Action.REDUCE;
        }
    }

    public List<RuleInProgress0> predict() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItemType.NON_TERMINAL;
        return rules.get(currentItem.getValue()).stream()
                        .map(cr -> new RuleInProgress0(cr.getTarget(), cr.getItems(), 0, rules)).collect(Collectors.toList());
    }

    public List<RuleInProgress0> shift() {
        return Collections.singletonList(new RuleInProgress0(target, items, offset + 1, rules));
    }

    public List<RuleInProgress0> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        RuleItem currentItem = items.get(offset);
        if (!token.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new RuleInProgress0(target, items, offset + 1, rules));
    }


    public List<RuleInProgress0> expand() {
        RuleItem currentItem = items.get(offset);
        List<RuleInProgress0> newNodes = new ArrayList<>();
        for (List<RuleItem> expansion: currentItem.expand()) {
            List<RuleItem> expanded = new ArrayList<>();
            expanded.addAll(items.subList(0, offset));
            expanded.addAll(expansion);
            expanded.addAll(items.subList(offset +1, items.size()));
            newNodes.add(new RuleInProgress0(target, expanded, offset, rules));
        }
        return newNodes;
    }

    public int getTarget() {
        return target;
    }

    public List<RuleItem> getItems() {
        return items;
    }

    public int getOffset() {
        return offset;
    }

    public String toString(Vocabulary grammar) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(target == -1 ? "$" : grammar.getNonTerminalName(target))
                .append(" =");
        for (int i = 0; i < items.size(); i ++) {
            if (i == offset) {
                stringBuilder.append(" ^ ");
            } else {
                stringBuilder.append(" ");
            }
            stringBuilder.append(items.get(i).toString(grammar));
        }
        if (offset == items.size()) {
            stringBuilder.append(" ^");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RuleInProgress0 that = (RuleInProgress0) o;
        return target == that.target &&
                offset == that.offset &&
                items.equals(that.items);
    }


    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            h = target;
            h = h * 31 + items.hashCode();
            h = h * 31 + offset;
            hash = h;
        }
        return h;
    }

    private int hash;

}
