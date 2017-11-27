package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.*;

/**
 * @author lvovich
 */
public class NapaChartNode {

    private final RuleInProgress ruleInProgress;
    private final List<NapaChartNode> enclosing;

    public NapaChartNode(final RuleInProgress ruleInProgress, final List<NapaChartNode> enclosing) {
        this.ruleInProgress = ruleInProgress;
        this.enclosing = enclosing;
    }

    public NapaChartNode merge(NapaChartNode other) {
        if (this == other || other == null) {
            return this;
        } else if (ruleInProgress.equals(other.ruleInProgress)) {
            List<NapaChartNode> mergedEnclosing = new ArrayList<>(enclosing);
            mergedEnclosing.addAll(other.enclosing);
            return new NapaChartNode(ruleInProgress, mergedEnclosing);
        } else {
            throw new IllegalArgumentException("Key mismatch");
        }
    }

    public List<RawSyntaxNode> accept() {
        if (enclosing.isEmpty()) {
            return ruleInProgress.accept();
        } else {
            return Collections.emptyList();
        }
    }

    public List<NapaChartNode> predict(Token<TokenProperties> lookAhead) {
        List<RuleInProgress> predicted = ruleInProgress.predict(lookAhead);
        List<NapaChartNode> newNodes = new ArrayList<>(predicted.size());
        List<NapaChartNode> thisNode = Collections.singletonList(this);
        for (RuleInProgress rule: predicted) {
            newNodes.add(new NapaChartNode(rule, thisNode));
        }
        return newNodes;
    }

    public List<NapaChartNode> reduce(Token<TokenProperties> lookAhead) {
        if (enclosing.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<NapaChartNode> result = new ArrayList<>();
            ruleInProgress.reduce(lookAhead).ifPresent(s -> {
                for (NapaChartNode parent: new LinkedHashSet<NapaChartNode>(enclosing)) {
                    result.addAll(parent.acceptNonTerminal(s));
                }
            });
            return result;
        }
    }

    private List<NapaChartNode> acceptNonTerminal(RawSyntaxNode node) {
        List<RuleInProgress> newRules = ruleInProgress.acceptNonTerminal(node);
        List<NapaChartNode> newNodes = new ArrayList<>(newRules.size());
        for (RuleInProgress rule: newRules) {
            newNodes.add(new NapaChartNode(rule, enclosing));
        }
        return newNodes;
    }

    public List<NapaChartNode> expand(Token<TokenProperties> lookAhead) {
        List<NapaChartNode> expanded = new ArrayList<>();
        for (RuleInProgress rule: ruleInProgress.expand(lookAhead)) {
            expanded.add(new NapaChartNode(rule, enclosing));
        }
        return expanded;
    }

    public List<NapaChartNode> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        List<RuleInProgress> shifted = ruleInProgress.shift(token, preface);
        List<NapaChartNode> newNodes = new ArrayList<>(shifted.size());
        for (RuleInProgress rule: shifted) {
            newNodes.add(new NapaChartNode(rule, enclosing));
        }
        return newNodes;
    }

    @Override
    public String toString() {
        return ruleInProgress.toString();
    }

    public RuleInProgress getRuleInProgress() {
        return ruleInProgress;
    }

    List<NapaChartNode> getEnclosing() {
        return enclosing;
    }

    public boolean isStartNode() {
        return enclosing.isEmpty();
    }

}
