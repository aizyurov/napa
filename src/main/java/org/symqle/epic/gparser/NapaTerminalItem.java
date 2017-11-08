package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class NapaTerminalItem implements NapaRuleItem {

    private final int value;

    private final CompiledGrammar grammar;

    public NapaTerminalItem(final int value, CompiledGrammar grammar) {
        this.value = value;
        this.grammar = grammar;
    }

    @Override
    public RuleItemType getType() {
        return RuleItemType.TERMINAL;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return grammar.getTerminalName(value);
    }

    @Override
    public boolean hasEmptyDerivation() {
        return false;
    }

    @Override
    public Set<Integer> first() {
        return Collections.singleton(value);
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }

    @Override
    public List<List<NapaRuleItem>> predict(final Token<TokenProperties> lookAhead) {
        return Collections.emptyList();
    }


}
