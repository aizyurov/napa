package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ChartNode {

    public enum Action {
        PREDICT,
        REDUCE,
        EXPAND,
        SHIFT,
        ACCEPT
    }

    private final int target;
    private final List<RuleItem> items;
    private int offset;
    private final ChartNode enclosing;
    private final List<SyntaxTreeNode> syntaxNodes;
    private final CompiledGrammar compiledGrammar;

    public ChartNode(final int target, final List<RuleItem> items, final int offset, final ChartNode enclosing, final List<SyntaxTreeNode> syntaxNodes, CompiledGrammar compiledGrammar) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.enclosing = enclosing;
        this.syntaxNodes = new ArrayList<>(syntaxNodes);
        this.compiledGrammar = compiledGrammar;
    }

    public Action availableAction() {
        if (offset < items.size()) {
            switch (items.get(offset).getType()) {
                case NON_TERMINAL:
                    return Action.PREDICT;
                case TERMINAL:
                    return Action.SHIFT;
                case COMPOUND:
                    return Action.EXPAND;
                default:
                    throw new IllegalStateException("Should never get here");
            }
        } else {
            return enclosing != null ? Action.REDUCE : Action.ACCEPT;
        }
    }

    public SyntaxTreeNode accept() {
        assert availableAction() == Action.ACCEPT;
        return syntaxNodes.get(0);
    }

    public List<ChartNode> predict() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItemType.NON_TERMINAL;
        return compiledGrammar.getRules(currentItem.getValue()).stream()
                        .map(cr -> new ChartNode(cr.getTarget(), cr.getItems(), 0, this, Collections.emptyList(), compiledGrammar)).collect(Collectors.toList());
    }

    public List<ChartNode> reduce() {
        assert offset == items.size();
        NonTerminalNode newNode = new NonTerminalNode(compiledGrammar.getNonTerminalName(target),
                syntaxNodes);
        return enclosing.acceptNonTerminal(newNode);
    }

    private List<ChartNode> acceptNonTerminal(NonTerminalNode node) {
        List<SyntaxTreeNode> nodes = new ArrayList<>(syntaxNodes);
        nodes.add(node);
        return Collections.singletonList(new ChartNode(target, items, offset + 1, enclosing, nodes, compiledGrammar));
    }

    public List<ChartNode> expand() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItemType.COMPOUND;
        List<ChartNode> newNodes = new ArrayList<>();
        for (List<RuleItem> expansion: currentItem.expand()) {
            List<RuleItem> expanded = new ArrayList<>();
            expanded.addAll(items.subList(0, offset));
            expanded.addAll(expansion);
            expanded.addAll(items.subList(offset +1, items.size()));
            newNodes.add(new ChartNode(target, expanded, offset, enclosing, new ArrayList<>(syntaxNodes), compiledGrammar));
        }
        return newNodes;
    }

    public List<ChartNode> shift(Token<TokenProperties> tokenProperties, List<String> preface) {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItemType.TERMINAL;
        if (!tokenProperties.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        offset += 1;
        syntaxNodes.add(new TerminalNode(compiledGrammar.getTerminalName(currentItem.getValue()), preface, tokenProperties.getText(), null, tokenProperties.getLine(), tokenProperties.getPos()));
        return Collections.singletonList(this);
    }

    public ChartNode getEnclosing() {
        return enclosing;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (target >= 0) {
            stringBuilder.append(compiledGrammar.getNonTerminalName(target)).append(" =");
        }
        for (int i = 0; i<items.size(); i++) {
            if (offset == i) {
                stringBuilder.append(" ^");
            }
            final RuleItem item = items.get(i);
            stringBuilder.append(" ").append(item.toString(compiledGrammar));
        }
        if (offset == items.size()) {
            stringBuilder.append(" ^");
        }
        return stringBuilder.toString();
    }
}
