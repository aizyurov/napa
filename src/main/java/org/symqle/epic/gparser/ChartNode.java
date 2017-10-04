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

    public List<ChartNode> predict(CompiledGrammar compiledGrammar) {
        if (offset >=items.size()) {
            return Collections.emptyList();
        }
        RuleItem currentItem = items.get(offset);
        if (currentItem.getType() != RuleItem.Type.NON_TERMINAL) {
            return Collections.emptyList();
        }
        return compiledGrammar.getRules(currentItem.getValue()).stream()
                .map(cr -> new ChartNode(cr.getTarget(), cr.getItems(), 0, this, Collections.emptyList())).collect(Collectors.toList());
    }

    public List<ChartNode> reduce(CompiledGrammar compiledGrammar) {
        if (offset < items.size()) {
            return Collections.emptyList();
        } else {
            NonTerminalNode newNode = new NonTerminalNode(compiledGrammar.getNonTerminalName(target),
                    syntaxNodes);
            return enclosing.acceptNonTerminal(newNode);
        }
    }

    private List<ChartNode> acceptNonTerminal(NonTerminalNode node) {
        List<SyntaxTreeNode> nodes = new ArrayList<>(syntaxNodes);
        nodes.add(node);
        return Collections.singletonList(new ChartNode(target, items, offset + 1, enclosing, nodes));
    }

    public List<ChartNode> expand() {
        if (offset >=items.size()) {
            return Collections.emptyList();
        }
        RuleItem currentItem = items.get(offset);
        switch(currentItem.getType()) {
            case TERMINAL: case NON_TERMINAL:
                return Collections.emptyList();
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
                throw new IllegalArgumentException("Unknown type: " + currentItem.getType());
        }
    }

    public List<ChartNode> noshift(CompiledGrammar compiledGrammar) {
        return Stream.concat(predict(compiledGrammar).stream(), Stream.concat(expand().stream(), reduce(compiledGrammar).stream())).collect(Collectors.toList());
    }

    public List<ChartNode> shift(ParserToken parserToken, CompiledGrammar compiledGrammar) {
        if (offset >=items.size()) {
            return Collections.emptyList();
        }
        RuleItem currentItem = items.get(offset);
        if (currentItem.getType() != RuleItem.Type.TERMINAL) {
            return Collections.emptyList();
        }
        if (!parserToken.matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        offset += 1;
        syntaxNodes.add(new TerminalNode(compiledGrammar.getTerminalName(currentItem.getValue()), parserToken.preface(), parserToken.text(), null, parserToken.line(), parserToken.pos()));
        return Collections.singletonList(this);
    }
}
