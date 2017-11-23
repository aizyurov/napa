package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class NapaChartNode {

    private final RuleInProgress ruleInProgress;
    private final Set<NapaChartNode> enclosing;

    public NapaChartNode(final RuleInProgress ruleInProgress, final Set<NapaChartNode> enclosing) {
        this.ruleInProgress = ruleInProgress;
        this.enclosing = new HashSet<>(enclosing);
    }

    public NapaChartNode merge(NapaChartNode other) {
        if (this == other || other == null) {
            return this;
        } else if (ruleInProgress.equals(other.ruleInProgress)) {
            Set<NapaChartNode> mergedEnclosing = new HashSet<>(enclosing);
            mergedEnclosing.addAll(other.enclosing);
            return new NapaChartNode(ruleInProgress, mergedEnclosing);
        } else {
            throw new IllegalArgumentException("Key mismatch");
        }
    }

    public ProcessingResult process(Token<TokenProperties> lookAhead) {
        List<NapaChartNode> noShift = new ArrayList<>();
        List<NapaChartNode> shift = new ArrayList<>();
        List<RawSyntaxNode> accepted = new ArrayList<>();
        sortNodes(lookAhead, noShift, shift, predict(lookAhead));
        sortNodes(lookAhead, noShift, shift, expand(lookAhead));
        sortNodes(lookAhead, noShift, shift, reduce(lookAhead));
        if (getRuleInProgress().canShift(lookAhead)) {
            shift.add(this);
        }
        accepted.addAll(accept());
        return new ProcessingResult(noShift, shift,
                accepted);
    }

    private void sortNodes(final Token<TokenProperties> lookAhead, final List<NapaChartNode> noShift, final List<NapaChartNode> shift, final List<NapaChartNode> predicted) {
        for (NapaChartNode node: predicted) {
            if (!node.getRuleInProgress().beforeTerminal(lookAhead)) {
                noShift.add(node);
            } else if (node.getRuleInProgress().canShift(lookAhead)) {
                shift.add(node);
            }
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
        Set<NapaChartNode> thisNode = Collections.singleton(this);
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
                for (NapaChartNode parent: enclosing) {
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

    public Set<NapaChartNode> getEnclosing() {
        return enclosing;
    }
}
