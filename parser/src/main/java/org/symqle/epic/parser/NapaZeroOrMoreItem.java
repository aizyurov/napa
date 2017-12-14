package org.symqle.epic.parser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class NapaZeroOrMoreItem extends AbstractNapaCompoundItem {

    public NapaZeroOrMoreItem(final List<List<NapaRuleItem>> options, boolean canBeEmpty, Set<Integer> first) {
        super(options, canBeEmpty, first);
    }

    @Override
    public List<List<NapaRuleItem>> expand(Token<TokenProperties> lookAhead) {
        List<List<NapaRuleItem>> expansion = new ArrayList<>();
        expansion.add(Collections.emptyList()); // zero
        for (List<NapaRuleItem> option: super.expand(lookAhead)) {
            ArrayList<NapaRuleItem> sequence = new ArrayList<>(option);
            sequence.add(this);
            expansion.add(sequence);
        }
        return expansion;
    }

    @Override
    public String toString() {
        return "{" + optionsToString() + "}";
    }
}
