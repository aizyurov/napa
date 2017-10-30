package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.symqle.epic.gparser.RuleItemType.COMPOUND;
import static org.symqle.epic.gparser.RuleItemType.TERMINAL;

/**
 * @author lvovich
 */
public class ChartNode {

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

    public Action availableAction(Token<TokenProperties> token) {
        if (offset < items.size()) {
            final RuleItem item = items.get(offset);
            switch (item.getType()) {
                case NON_TERMINAL:
                    if (compiledGrammar.hasEmptyDerivation(item.getValue())) {
                        return Action.PREDICT;
                    }
                    if (token == null) {
                        // end of input and no empty derivation
                        return Action.DROP;
                    }
                    if (token.getType().isIgnorable()) {
                        return Action.PREDICT;
                    }
                    final Set<Integer> expectedTags = compiledGrammar.getFirstSet(item.getValue());
                    return token.getType().matches(expectedTags) ? Action.PREDICT : Action.DROP;
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
            return enclosing != null ? Action.REDUCE : Action.ACCEPT;
        }
    }

    public SyntaxTreeNode accept() {
        if (offset == items.size() && enclosing == null) {
            return syntaxNodes.get(0);
        } else {
            return null;
        }
    }

    public List<ChartNode> predict() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItemType.NON_TERMINAL;
        return compiledGrammar.getRules(currentItem.getValue()).stream()
                        .map(cr -> new ChartNode(cr.getTarget(), cr.getItems(), 0, this, Collections.emptyList(), compiledGrammar)).collect(Collectors.toList());
    }

    public List<ChartNode> reduce() {
        assert offset == items.size() && enclosing != null;
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
        assert currentItem.getType() == COMPOUND;
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
        assert currentItem.getType() == TERMINAL;
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
