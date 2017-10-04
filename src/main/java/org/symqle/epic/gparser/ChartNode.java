package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public ChartNode(final int target, final List<RuleItem> items, final int offset, final ChartNode enclosing, final List<SyntaxTreeNode> syntaxNodes) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.enclosing = enclosing;
        this.syntaxNodes = syntaxNodes;
    }

    public Action availableAction() {
        if (offset < items.size()) {
            switch (items.get(offset).getType()) {
                case NON_TERMINAL:
                    return Action.PREDICT;
                case TERMINAL:
                    return Action.SHIFT;
                case ZERO_OR_ONE: case ZERO_OR_MORE:
                    return Action.EXPAND;
                default:
                    throw new IllegalStateException("Should never get here");
            }
        } else {
            return target >=0 ? Action.REDUCE : Action.ACCEPT;
        }
    }

    public SyntaxTreeNode accept() {
        assert availableAction() == Action.ACCEPT;
        return syntaxNodes.get(0);
    }

    public List<ChartNode> predict(CompiledGrammar compiledGrammar) {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItem.Type.NON_TERMINAL;
        return compiledGrammar.getRules(currentItem.getValue()).stream()
                .map(cr -> new ChartNode(cr.getTarget(), cr.getItems(), 0, this, Collections.emptyList())).collect(Collectors.toList());
    }

    public List<ChartNode> reduce(CompiledGrammar compiledGrammar) {
        assert offset == items.size();
        NonTerminalNode newNode = new NonTerminalNode(compiledGrammar.getNonTerminalName(target),
                syntaxNodes);
        return enclosing.acceptNonTerminal(newNode);
    }

    private List<ChartNode> acceptNonTerminal(NonTerminalNode node) {
        List<SyntaxTreeNode> nodes = new ArrayList<>(syntaxNodes);
        nodes.add(node);
        return Collections.singletonList(new ChartNode(target, items, offset + 1, enclosing, nodes));
    }

    public List<ChartNode> expand() {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        switch(currentItem.getType()) {
            case ZERO_OR_ONE:
                List<ChartNode> newNodes = new ArrayList<>(2);
            {
                //zero
                List<RuleItem> expansion = new ArrayList<>();
                expansion.addAll(items.subList(0, offset));
                expansion.addAll(items.subList(offset +1, items.size()));
                newNodes.add(new ChartNode(target, expansion, offset, enclosing, new ArrayList<>(syntaxNodes)));
            }
            {
                // one
                List<RuleItem> expansion = new ArrayList<>();
                expansion.addAll(items.subList(0, offset));
                expansion.addAll(currentItem.getItems());
                expansion.addAll(items.subList(offset +1, items.size()));
                newNodes.add(new ChartNode(target, expansion, offset, enclosing, new ArrayList<>(syntaxNodes)));
            }
            return newNodes;
            case ZERO_OR_MORE:
                List<ChartNode> moreNodes = new ArrayList<>(2);
            {
                // zero
                List<RuleItem> expansion = new ArrayList<>();
                expansion.addAll(items.subList(0, offset));
                expansion.addAll(items.subList(offset +1, items.size()));
                moreNodes.add(new ChartNode(target, expansion, offset, enclosing, new ArrayList<>(syntaxNodes)));
            }
            {
                // more
                List<RuleItem> expansion = new ArrayList<>();
                expansion.addAll(items.subList(0, offset));
                expansion.add(currentItem);
                expansion.addAll(items.subList(offset +1, items.size()));
                moreNodes.add(new ChartNode(target, expansion, offset, enclosing, new ArrayList<>(syntaxNodes)));
            }
            return moreNodes;
            default:
                assert false;
                return null;
        }
    }

    public List<ChartNode> shift(ParserToken parserToken, CompiledGrammar compiledGrammar) {
        assert offset < items.size();
        RuleItem currentItem = items.get(offset);
        assert currentItem.getType() == RuleItem.Type.TERMINAL;
        if (!parserToken.matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        offset += 1;
        syntaxNodes.add(new TerminalNode(compiledGrammar.getTerminalName(currentItem.getValue()), parserToken.preface(), parserToken.text(), null, parserToken.line(), parserToken.pos()));
        return Collections.singletonList(this);
    }
}
