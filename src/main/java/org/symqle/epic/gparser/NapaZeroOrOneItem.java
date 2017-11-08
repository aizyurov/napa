package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class NapaZeroOrOneItem extends AbstractNapaCompoundItem {

    public NapaZeroOrOneItem(final List<List<NapaRuleItem>> options) {
        super(options);
    }

    @Override
    public List<List<NapaRuleItem>> expand(Token<TokenProperties> lookAhead) {
        List<List<NapaRuleItem>> expansion = new ArrayList<>();
        expansion.add(Collections.emptyList()); // zero
        expansion.addAll(super.expand(lookAhead));
        return expansion;
    }

    @Override
    public boolean hasEmptyDerivation() {
        return true;
    }

    @Override
    public String toString() {
        return "(" + optionsToString() + ")";
    }
}
