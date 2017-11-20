package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.symqle.epic.gparser.RuleItemType.TERMINAL;

/**
 * @author lvovich
 */
public class RuleInProgress {

    private final int target;
    private final NapaRuleItem[] items;
    private final int offset;
    private final RawSyntaxNode[] syntaxNodes;
    private final CompiledGrammar grammar;

    private static final RawSyntaxNode[] NO_NODES = {};
    private static final RuleInProgress[] NO_RULES = {};

    // Note: no protective copying. The caller should not modify items and syntaxNodes, provide a copy when necessary.
    private RuleInProgress(final int target, final NapaRuleItem[] items, final int offset, final RawSyntaxNode[] syntaxNodes, CompiledGrammar grammar) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.syntaxNodes = syntaxNodes;
        this.grammar = grammar;
    }

    static RuleInProgress startRule(NapaRuleItem targetItem, CompiledGrammar grammar) {
        return new RuleInProgress(-1, new NapaRuleItem[]{targetItem}, 0, NO_NODES, grammar);
    }

    public List<RawSyntaxNode> accept() {
        if (offset == items.length) {
            return Collections.singletonList(syntaxNodes[0]);
        } else {
            return Collections.emptyList();
        }
    }

    public List<RuleInProgress> predict(Token<TokenProperties> lookAhead) {
        if (offset < items.length) {
            NapaRuleItem currentItem = items[offset];
            List<List<NapaRuleItem>> predict = currentItem.predict(lookAhead);
            if (predict.isEmpty()) {
                return Collections.emptyList();
            } else {
                List<RuleInProgress>  result = new ArrayList<>(predict.size());
                for (List<NapaRuleItem> items : predict) {
                    result.add(new RuleInProgress(currentItem.getValue(), items.toArray(new NapaRuleItem[items.size()]), 0, NO_NODES, grammar));
                }
                return result;
            }
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<RawSyntaxNode> reduce(Token<TokenProperties> lookAhead) {
        if (offset == items.length) {
            RawSyntaxNode newNode;
            if (syntaxNodes.length == 0) {
                newNode = new EmptyNode(target, lookAhead.getLine(), lookAhead.getPos());
            } else {
                newNode = new NonTerminalNode(target, Arrays.asList(syntaxNodes));
            }
            return Optional.of(newNode);
        } else {
            return Optional.empty();
        }
    }

    public List<RuleInProgress> acceptNonTerminal(RawSyntaxNode node) {
        int length = syntaxNodes.length;
        RawSyntaxNode[] nodes = new RawSyntaxNode[length + 1];
        System.arraycopy(syntaxNodes, 0, nodes, 0, length);
        nodes[length] = node;
        return Collections.singletonList(new RuleInProgress(target, items, offset + 1, nodes, grammar));
    }

    public List<RuleInProgress> expand(Token<TokenProperties> lookAhead) {
        if (offset >= items.length) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items[offset];
        List<RuleInProgress> newNodes = new ArrayList<>();
        for (List<NapaRuleItem> expansion: currentItem.expand(lookAhead)) {
            NapaRuleItem[] expanded = new NapaRuleItem[items.length + expansion.size() - 1];
            System.arraycopy(items, 0, expanded, 0, offset);
            for (int i = 0; i < expansion.size(); i ++) {
                expanded[offset + i] = expansion.get(i);
            }
            System.arraycopy(items, offset + 1, expanded, offset + expansion.size(), items.length - offset - 1);
            newNodes.add(new RuleInProgress(target, expanded, offset, syntaxNodes, grammar));
        }
        return newNodes;
    }

    public boolean canShift(Token<TokenProperties> lookAhead) {
        if (offset >= items.length || lookAhead == null) {
            return false;
        }
        NapaRuleItem currentItem = items[offset];
        return currentItem.getType() == TERMINAL && lookAhead.getType().matches(currentItem.first());
    }

    public List<RuleInProgress> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        if (offset >= items.length) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items[offset];
        assert currentItem.getType() == TERMINAL;
        if (!token.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        RawSyntaxNode[] newSyntaxNodes = new RawSyntaxNode[syntaxNodes.length + 1];
        System.arraycopy(syntaxNodes, 0, newSyntaxNodes, 0, syntaxNodes.length);
        newSyntaxNodes[syntaxNodes.length] = new TerminalNode(currentItem.getValue(), preface, token);
        return Collections.singletonList(new RuleInProgress(target, items, offset + 1, newSyntaxNodes, grammar));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (target >= 0) {
            stringBuilder.append(grammar.getNonTerminalName(target)).append(" =");
        }
        for (int i = 0; i<items.length; i++) {
            if (offset == i) {
                stringBuilder.append(" ^");
            }
            final NapaRuleItem item = items[i];
            stringBuilder.append(" ").append(item.toString());
        }
        if (offset == items.length) {
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
                Arrays.equals(items, that.items) &&
                Arrays.equals(syntaxNodes, that.syntaxNodes);
    }


    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            h = target;
            h = h * 31 + Arrays.hashCode(items);
            h = h * 31 + offset;
            h = h * 31 + Arrays.hashCode(syntaxNodes);
            hash = h;
        }
        return h;
    }

    private int hash;
}
