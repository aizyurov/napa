package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.symqle.epic.gparser.RuleItemType.TERMINAL;

/**
 * @author lvovich
 */
public class NapaChartNode {

    private final int target;
    private final List<NapaRuleItem> items;
    private int offset;
    private final NapaChartNode enclosing;
    private final List<RawSyntaxNode> syntaxNodes;
    private final CompiledGrammar grammar;

    public NapaChartNode(final int target, final List<NapaRuleItem> items, final int offset, final NapaChartNode enclosing, final List<RawSyntaxNode> syntaxNodes, CompiledGrammar grammar) {
        this.target = target;
        this.items = items;
        this.offset = offset;
        this.enclosing = enclosing;
        this.syntaxNodes = new ArrayList<>(syntaxNodes);
        this.grammar = grammar;
    }

    public ProcessingResult process(Token<TokenProperties> lookAhead) {
        List<NapaChartNode> noShift = new ArrayList<>();
        List<NapaChartNode> shiftCandidates = new ArrayList<>();
        List<RawSyntaxNode> accepted = new ArrayList<>();
        noShift.addAll(predict(lookAhead));
        noShift.addAll(expand(lookAhead));
        noShift.addAll(reduce(lookAhead));
        shiftCandidates.addAll(canShift(lookAhead));
        accepted.addAll(accept());
        return new ProcessingResult(noShift, shiftCandidates, accepted);
    }

    public List<RawSyntaxNode> accept() {
        if (offset == items.size() && enclosing == null) {
            return Collections.singletonList(syntaxNodes.get(0));
        } else {
            return Collections.emptyList();
        }
    }

    public List<NapaChartNode> predict(Token<TokenProperties> lookAhead) {
        if (offset < items.size()) {
            NapaRuleItem currentItem = items.get(offset);
            return currentItem.predict(lookAhead).stream().map(items -> new NapaChartNode(currentItem.getValue(), items, 0, this, Collections.emptyList(), grammar)).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public List<NapaChartNode> reduce(Token<TokenProperties> lookAhead) {
        if (offset == items.size() && enclosing != null) {
            RawSyntaxNode newNode;
            if (syntaxNodes.isEmpty()) {
                newNode = new EmptyNode(target, lookAhead.getLine(), lookAhead.getPos());
            } else {
                newNode = new NonTerminalNode(target, syntaxNodes);
            }
            return enclosing.acceptNonTerminal(newNode);
        } else {
            return Collections.emptyList();
        }
    }

    private List<NapaChartNode> acceptNonTerminal(RawSyntaxNode node) {
        List<RawSyntaxNode> nodes = new ArrayList<>(syntaxNodes);
        nodes.add(node);
        return Collections.singletonList(new NapaChartNode(target, items, offset + 1, enclosing, nodes, grammar));
    }

    public List<NapaChartNode> expand(Token<TokenProperties> lookAhead) {
        if (offset >= items.size()) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items.get(offset);
        List<NapaChartNode> newNodes = new ArrayList<>();
        for (List<NapaRuleItem> expansion: currentItem.expand(lookAhead)) {
            List<NapaRuleItem> expanded = new ArrayList<>();
            expanded.addAll(items.subList(0, offset));
            expanded.addAll(expansion);
            expanded.addAll(items.subList(offset +1, items.size()));
            newNodes.add(new NapaChartNode(target, expanded, offset, enclosing, new ArrayList<>(syntaxNodes), grammar));
        }
        return newNodes;
    }

    public List<NapaChartNode> canShift(Token<TokenProperties> lookAhead) {
        if (offset >= items.size() || lookAhead == null) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items.get(offset);
        return currentItem.getType() == TERMINAL && lookAhead.getType().matches(currentItem.first())
                ? Collections.singletonList(this)
                : Collections.emptyList();
    }

    public List<NapaChartNode> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        if (offset >= items.size()) {
            return Collections.emptyList();
        }
        NapaRuleItem currentItem = items.get(offset);
        assert currentItem.getType() == TERMINAL;
        if (!token.getType().matches(currentItem.getValue())) {
            return Collections.emptyList();
        }
        offset += 1;
        syntaxNodes.add(new TerminalNode(currentItem.getValue(), preface, token));
        return Collections.singletonList(this);
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
}
