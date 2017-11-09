package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.symqle.epic.gparser.RuleItemType.COMPOUND;
import static org.symqle.epic.gparser.RuleItemType.TERMINAL;

/**
 * @author lvovich
 */
public class ChartNode0 {

    public enum Action {
        PREDICT,
        REDUCE,
        EXPAND,
        SHIFT,
        ACCEPT,
        DROP
    }

    private final int target;
    private final List<RuleItem> items;
    private int offset;
    private final ChartNode0 enclosing;
    private final Map<Integer, List<CompiledRule>> rules;

    public ChartNode0(final int target, final List<RuleItem> items, final int offset, final ChartNode0 enclosing, Map<Integer, List<CompiledRule>> rules) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.enclosing = enclosing;
        this.rules = rules;
    }

    public Action availableAction(Token<TokenProperties> token) {
        if (offset < items.size()) {
            final RuleItem item = items.get(offset);
            switch (item.getType()) {
                case NON_TERMINAL:
                    if (token == null) {
                        // end of input and no empty derivation
                        return Action.DROP;
                    } else {
                        return Action.PREDICT;
                    }
                case TERMINAL:
                    if (token == null) {
                        // end of input but terminal expected
                        return Action.DROP;
                    }
                    TokenProperties type = token.getType();
                    return type.matches(item.getValue()) ? Action.SHIFT : Action.DROP;
                case COMPOUND:
                    return Action.EXPAND;
                default:
                    throw new IllegalStateException("Should never get here");
            }
        } else {
            // always, even at the end of input
            if (enclosing != null) return Action.REDUCE;
            else return Action.ACCEPT;
        }
    }

    public List<ChartNode0> predict() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItemType.NON_TERMINAL;
        return rules.get(currentItem.getValue()).stream()
                        .map(cr -> new ChartNode0(cr.getTarget(), cr.getItems(), 0, this, rules)).collect(Collectors.toList());
    }

    public List<ChartNode0> reduce(Token<?> nextToken) {
        assert offset == items.size() && enclosing != null;
        return enclosing.acceptNonTerminal();
    }

    private List<ChartNode0> acceptNonTerminal() {
        return Collections.singletonList(new ChartNode0(target, items, offset + 1, enclosing, rules));
    }

    public List<ChartNode0> expand() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == COMPOUND;
        List<ChartNode0> newNodes = new ArrayList<>();
        for (List<RuleItem> expansion: currentItem.expand()) {
            List<RuleItem> expanded = new ArrayList<>();
            expanded.addAll(items.subList(0, offset));
            expanded.addAll(expansion);
            expanded.addAll(items.subList(offset +1, items.size()));
            newNodes.add(new ChartNode0(target, expanded, offset, enclosing, rules));
        }
        return newNodes;
    }

    public List<ChartNode0> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == TERMINAL;
        if (!token.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        offset += 1;
        return Collections.singletonList(this);
    }

    public ChartNode0 getEnclosing() {
        return enclosing;
    }

}
