package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

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

    public ChartNode0(final RuleInProgress0 ruleInProgress, final ChartNode0 enclosing) {
        this(ruleInProgress, enclosing == null ? Collections.emptySet() : Collections.singleton(enclosing));
    }

    private ChartNode0(final RuleInProgress0 ruleInProgress, final Set<ChartNode0> enclosing) {
        this.ruleInProgress = ruleInProgress;
        this.enclosing = enclosing;
    }

    public ChartNode0 merge(ChartNode0 other) {
        if (other == null) {
            return this;
        }
        if (!ruleInProgress.equals(other.ruleInProgress)) {
            throw new IllegalArgumentException("Different rules");
        }
        Set<ChartNode0> newEnclosing = new HashSet<>(enclosing);
        newEnclosing.addAll(other.enclosing);
        return new ChartNode0(ruleInProgress, newEnclosing);
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
        return ruleInProgress.shift().stream().map(r -> new ChartNode0(r, enclosing)).collect(Collectors.toList());
    }

    public List<ChartNode0> expand() {
        return ruleInProgress.expand().stream().map(r -> new ChartNode0(r, enclosing)).collect(Collectors.toList());
    }

    public List<ChartNode0> shift(Token<TokenProperties> token, List<Token<TokenProperties>> preface) {
        return ruleInProgress.shift().stream().map(r -> new ChartNode0(r, enclosing)).collect(Collectors.toList());
    }

    public List<ChartNode0> accept() {
        return Collections.singletonList(this);
    }

    public RuleInProgress0 getRuleInProgress() {
        return ruleInProgress;
    }

    public String format(CompiledGrammar grammar) {
        return ruleInProgress.toString(grammar);
    }

}
