package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.symqle.napa.parser.RuleItemType.TERMINAL;

/**
 * @author lvovich
 */
public class RuleInProgress {

    private final int target;
    private final List<NapaRuleItem> items;
    private final int offset;
    private final AppendableList<RawSyntaxNode> syntaxNodes;
    private final NapaRuleItem currentItem;

    private static final AppendableList<RawSyntaxNode> NO_NODES = AppendableList.empty();

    // Note: no protective copying. The caller should not modify items and syntaxNodes, provide a copy when necessary.
    private RuleInProgress(final int target, List<NapaRuleItem> items, final int offset, final AppendableList<RawSyntaxNode> syntaxNodes) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.syntaxNodes = syntaxNodes;
        this.currentItem = offset < items.size() ? items.get(offset) : null;

    }

    static RuleInProgress startRule(NapaRule rule, CompiledGrammar grammar) {
        return new RuleInProgress(rule.getTarget(), rule.getItems(), 0, NO_NODES);
    }

    public List<RuleInProgress> predict(Token<TokenProperties> lookAhead, CompiledGrammar grammar) {
        if (currentItem != null) {
            List<List<NapaRuleItem>> predict = currentItem.predict(lookAhead, grammar);
            final int size = predict.size();
            List<RuleInProgress>  result = new ArrayList<>(size);
            for (int i = 0; i< size; i++) {
                List<NapaRuleItem> items = predict.get(i);
                final RuleInProgress ruleInProgress = new RuleInProgress(currentItem.getValue(), items, 0, NO_NODES);
                result.add(ruleInProgress);
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public List<RawSyntaxNode> reduce(Token<TokenProperties> lookAhead, CompiledGrammar grammar) {
        if (currentItem == null) {
            RawSyntaxNode newNode;
            if (syntaxNodes == NO_NODES) {
                newNode = new EmptyNode(target, grammar.nonTerminalName(target), lookAhead.getLine(), lookAhead.getPos());
            } else {
                newNode = new NonTerminalNode(target, grammar.nonTerminalName(target), syntaxNodes.asList());
            }
            return Collections.singletonList(newNode);
        } else {
            return Collections.emptyList();
        }
    }

    public List<RuleInProgress> acceptNonTerminal(RawSyntaxNode node, Token<TokenProperties> lookAhead) {
        TokenProperties type = lookAhead.getType();

        for (int i = offset + 1; i < items.size() ; i++) {
            final NapaRuleItem item = items.get(i);
            if (type != null && type.matches(item.first())) {
                break;
            } else if (!item.hasEmptyDerivation()){
                return Collections.emptyList();
            }
        }
        AppendableList<RawSyntaxNode> nodes = this.syntaxNodes.append(node);
        return Collections.singletonList(new RuleInProgress(target, items, offset + 1, nodes));
    }

    public List<RuleInProgress> expand(Token<TokenProperties> lookAhead) {
        if (currentItem == null) {
            return Collections.emptyList();
        }
        List<RuleInProgress> newNodes = new ArrayList<>();
        for (List<NapaRuleItem> expansion: currentItem.expand(lookAhead)) {
            List<NapaRuleItem> expanded = new ArrayList<>(items.size() + expansion.size());
            expanded.addAll(items.subList(0, offset));
            expanded.addAll(expansion);
            expanded.addAll(items.subList(offset + 1, items.size()));
            newNodes.add(new RuleInProgress(target, expanded, offset, syntaxNodes));
        }
        return newNodes;
    }

    public List<RuleInProgress> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        if (currentItem == null) {
            return Collections.emptyList();
        }
        assert currentItem.getType() == TERMINAL;
        if (!token.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        AppendableList<RawSyntaxNode> newSyntaxNodes = this.syntaxNodes.append(new TerminalNode<TokenProperties>(currentItem.getValue(), currentItem.getName(), preface, token));
        return Collections.singletonList(new RuleInProgress(target, items, offset + 1, newSyntaxNodes));
    }

    public Action availableAction(Token<TokenProperties> lookAhead) {
        if (currentItem == null) {
            return Action.reduce;
        }
        TokenProperties tokenType = lookAhead.getType();
        final RuleItemType type = currentItem.getType();
        switch (type) {
            case TERMINAL:
                return tokenType != null && tokenType.matches(currentItem.first()) ? Action.shift : Action.none;
            case NON_TERMINAL:
                return currentItem.hasEmptyDerivation() || tokenType != null && tokenType.matches(currentItem.first()) ? Action.predict : Action.none;
            case COMPOUND:
                return currentItem.hasEmptyDerivation() || tokenType != null && tokenType.matches(currentItem.first()) ? Action.expand : Action.none;
            default:
                throw new IllegalArgumentException("Unexpected item type: " + type);
        }
    }

    public String toString(CompiledGrammar grammar) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(grammar.nonTerminalName(target)).append(" =");
        for (int i = 0; i<items.size(); i++) {
            if (offset == i) {
                stringBuilder.append(" ^");
            }
            final NapaRuleItem item = items.get(i);
            stringBuilder.append(" ").append(item.toString());
        }
        if (offset == items.size()) {
            stringBuilder.append(" ^");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(target).append(" =");
        for (int i = 0; i<items.size(); i++) {
            if (offset == i) {
                stringBuilder.append(" ^");
            }
            final NapaRuleItem item = items.get(i);
            stringBuilder.append(" ").append(item.toString());
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
        final RuleInProgress that = (RuleInProgress) o;
        return target == that.target &&
                offset == that.offset &&
                items.equals(that.items) &&
                syntaxNodes.equals(that.syntaxNodes);
    }


    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            h = target;
            h = h * 31 + items.hashCode();
            h = h * 31 + offset;
            h = h * 31 + syntaxNodes.hashCode();
            hash = h;
        }
        return h;
    }

    private int hash;

    public enum Action {
        predict,
        expand,
        reduce,
        shift,
        none
    }
}
