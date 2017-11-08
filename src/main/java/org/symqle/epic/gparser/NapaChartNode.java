package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (enclosing.isEmpty()) {
            return ruleInProgress.accept();
        } else {
            return Collections.emptyList();
        }
    }

    public List<NapaChartNode> predict(Token<TokenProperties> lookAhead) {
        return ruleInProgress.predict(lookAhead).stream().map(r -> new NapaChartNode(r, Collections.singleton(this))).collect(Collectors.toList());
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
        return newRules.stream().map(r -> new NapaChartNode(r, enclosing)).collect(Collectors.toList());
    }

    public List<NapaChartNode> expand(Token<TokenProperties> lookAhead) {
        return ruleInProgress.expand(lookAhead).stream().map(r -> new NapaChartNode(r, enclosing)).collect(Collectors.toList());
    }

    public List<NapaChartNode> canShift(Token<TokenProperties> lookAhead) {
        return ruleInProgress.canShift(lookAhead) ? Collections.singletonList(this) : Collections.emptyList();
    }

    public List<NapaChartNode> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        return ruleInProgress.shift(token, preface).stream().map(r -> new NapaChartNode(r, enclosing)).collect(Collectors.toList());
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
