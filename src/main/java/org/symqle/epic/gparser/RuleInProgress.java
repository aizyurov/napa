package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.symqle.epic.gparser.RuleItemType.TERMINAL;

/**
 * @author lvovich
 */
public class RuleInProgress {

    private final int target;
    private final List<NapaRuleItem> items;
    private final int offset;
    private final List<RawSyntaxNode> syntaxNodes;
    private final CompiledGrammar grammar;

    public RuleInProgress(final int target, final List<NapaRuleItem> items, final int offset, final List<RawSyntaxNode> syntaxNodes, CompiledGrammar grammar) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.syntaxNodes = new ArrayList<>(syntaxNodes);
        this.grammar = grammar;
    }

    public List<RawSyntaxNode> accept() {
        if (offset == items.size()) {
            return Collections.singletonList(syntaxNodes.get(0));
        } else {
            return Collections.emptyList();
        }
    }

    public List<RuleInProgress> predict(Token<TokenProperties> lookAhead) {
        if (offset < items.size()) {
            NapaRuleItem currentItem = items.get(offset);
            return currentItem.predict(lookAhead).stream().map(items -> new RuleInProgress(currentItem.getValue(), items, 0, Collections.emptyList(), grammar)).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public Optional<RawSyntaxNode> reduce(Token<TokenProperties> lookAhead) {
        if (offset == items.size()) {
            RawSyntaxNode newNode;
            if (syntaxNodes.isEmpty()) {
                newNode = new EmptyNode(target, lookAhead.getLine(), lookAhead.getPos());
            } else {
                newNode = new NonTerminalNode(target, syntaxNodes);
            }
            return Optional.of(newNode);
        } else {
            return Optional.empty();
        }
    }

    public List<RuleInProgress> acceptNonTerminal(RawSyntaxNode node) {
        List<RawSyntaxNode> nodes = new ArrayList<>(syntaxNodes);
        nodes.add(node);
        return Collections.singletonList(new RuleInProgress(target, items, offset + 1, nodes, grammar));
    }

    public List<RuleInProgress> expand(Token<TokenProperties> lookAhead) {
        if (offset >= items.size()) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items.get(offset);
        List<RuleInProgress> newNodes = new ArrayList<>();
        for (List<NapaRuleItem> expansion: currentItem.expand(lookAhead)) {
            List<NapaRuleItem> expanded = new ArrayList<>();
            expanded.addAll(items.subList(0, offset));
            expanded.addAll(expansion);
            expanded.addAll(items.subList(offset +1, items.size()));
            newNodes.add(new RuleInProgress(target, expanded, offset, new ArrayList<>(syntaxNodes), grammar));
        }
        return newNodes;
    }

    public boolean canShift(Token<TokenProperties> lookAhead) {
        if (offset >= items.size() || lookAhead == null) {
            return false;
        }
        NapaRuleItem currentItem = items.get(offset);
        return currentItem.getType() == TERMINAL && lookAhead.getType().matches(currentItem.first());
    }

    public List<RuleInProgress> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        if (offset >= items.size()) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items.get(offset);
        assert currentItem.getType() == TERMINAL;
        if (!token.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        List<RawSyntaxNode> newSyntaxNodes = new ArrayList<>(syntaxNodes);
        newSyntaxNodes.add(new TerminalNode(currentItem.getValue(), preface, token));
        return Collections.singletonList(new RuleInProgress(target, items, offset + 1, newSyntaxNodes, grammar));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (target >= 0) {
            stringBuilder.append(grammar.getNonTerminalName(target)).append(" =");
        }
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
                Objects.equals(items, that.items) &&
                Objects.equals(syntaxNodes, that.syntaxNodes) &&
                Objects.equals(grammar, that.grammar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, items, offset, syntaxNodes, grammar);
    }
}
