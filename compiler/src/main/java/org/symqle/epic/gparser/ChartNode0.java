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
public class ChartNode0 {

    private final RuleInProgress0 ruleInProgress;
    private final Set<ChartNode0> enclosing;
    private final List<Trace> traces;

    public ChartNode0(final RuleInProgress0 ruleInProgress, final ChartNode0 enclosing) {
        this(ruleInProgress, enclosing == null ? Collections.emptySet() : Collections.singleton(enclosing),
                enclosing == null ? Collections.singletonList(new Trace(ruleInProgress, null)) : enclosing.traces.stream().map(t -> new Trace(ruleInProgress, t)).collect(Collectors.toList()));
    }

    private ChartNode0(final RuleInProgress0 ruleInProgress, final Set<ChartNode0> enclosing, final List<Trace> traces) {
        this.ruleInProgress = ruleInProgress;
        this.enclosing = enclosing;
        this.traces = traces;
    }

    public ChartNode0 merge(ChartNode0 other) {
        if (other == null) {
            return this;
        }
        if (!ruleInProgress.equals(other.ruleInProgress)) {
            throw new IllegalArgumentException("Different rules");
        }
        Set<ChartNode0> mergedEnclosing = new HashSet<>(enclosing);
        mergedEnclosing.addAll(other.enclosing);
        List<Trace> mergedTraces = new ArrayList<>();
        mergedTraces.addAll(traces);
        mergedTraces.addAll(other.traces);
        return new ChartNode0(ruleInProgress, mergedEnclosing, mergedTraces);
    }

    public RuleInProgress0.Action availableAction() {
        RuleInProgress0.Action action = ruleInProgress.availableAction();
        switch (action) {
            case SHIFT:case PREDICT:case EXPAND:
                return action;
            case REDUCE:
                if (enclosing.isEmpty()) {
                    return RuleInProgress0.Action.ACCEPT;
                } else {
                    return action;
                }
        }
        throw new IllegalStateException("Should never get here");
    }
    public List<ChartNode0> predict() {
        return ruleInProgress.predict().stream()
                        .map(cr -> new ChartNode0(cr, this)).collect(Collectors.toList());
    }

    public List<ChartNode0> reduce() {
        return enclosing.stream().flatMap(c -> c.acceptNonTerminal().stream()).collect(Collectors.toList());
    }

    private List<ChartNode0> acceptNonTerminal() {
        return ruleInProgress.shift().stream().map(r -> new ChartNode0(r, enclosing, traces.stream().map(t -> new Trace(r,t)).collect(Collectors.toList()))).collect(Collectors.toList());
    }

    public List<ChartNode0> expand() {
        return ruleInProgress.expand().stream().map(r -> new ChartNode0(r, enclosing, traces.stream().map(t -> new Trace(r,t)).collect(Collectors.toList()))).collect(Collectors.toList());
    }

    public List<ChartNode0> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        return ruleInProgress.shift().stream().map(r -> new ChartNode0(r, enclosing, traces.stream().map(t -> new Trace(r,t)).collect(Collectors.toList()))).collect(Collectors.toList());
    }

    public List<ChartNode0> accept() {
        return Collections.singletonList(this);
    }

    public RuleInProgress0 getRuleInProgress() {
        return ruleInProgress;
    }

    public String format(Vocabulary grammar) {
        return ruleInProgress.toString(grammar);
    }

    public String trace(Vocabulary grammar) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Trace trace: traces) {
            appendHighlihtedTrace(stringBuilder, trace, null, grammar);
        }
        stringBuilder.append("-");
        return stringBuilder.toString();
    }

    private static class Trace {
        private final RuleInProgress0 ruleInProgress;
        private final Trace predecessor;

        public Trace(final RuleInProgress0 ruleInProgress, final Trace predecessor) {
            this.ruleInProgress = ruleInProgress;
            this.predecessor = predecessor;
        }

        public RuleInProgress0 getRuleInProgress() {
            return ruleInProgress;
        }

        public Trace getPredecessor() {
            return predecessor;
        }
    }

    public void infiniteRecursionCheck(Vocabulary grammar) {
        for (Trace trace: traces) {
            if (infiniteRecursion(trace)) {
                StringBuilder stringBuilder = new StringBuilder();
                appendTrace(stringBuilder, trace, grammar);
                throw new GrammarException("Infinite recursion:\n" + stringBuilder);
            }
        }
    }

    private boolean infiniteRecursion(Trace trace) {
        RuleInProgress0 ruleInProgress = trace.getRuleInProgress();
        for (Trace next = trace.getPredecessor(); next != null; next = next.getPredecessor()) {
            if (next.getRuleInProgress().equals(ruleInProgress)) {
                return true;
            }
        }
        return false;
    }

    private void appendTrace(StringBuilder stringBuilder, Trace last, Vocabulary grammar) {
        RuleInProgress0 duplicate = last.getRuleInProgress();
        appendHighlihtedTrace(stringBuilder, last, duplicate, grammar);
    }

    private void appendHighlihtedTrace(StringBuilder stringBuilder, Trace last, RuleInProgress0 duplicate, Vocabulary grammar) {
        if (last != null) {
            appendHighlihtedTrace(stringBuilder, last.getPredecessor(), duplicate, grammar);
            stringBuilder.append(last.getRuleInProgress().equals(duplicate) ? "*   " : "    ");
            stringBuilder.append(last.getRuleInProgress().toString(grammar));
            stringBuilder.append('\n');
        }
    }

}
