package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class NapaChoiceItem extends AbstractNapaCompoundItem {

    public NapaChoiceItem(final List<List<NapaRuleItem>> options, boolean canBeEmpty, Set<Integer> first) {
        super(options, canBeEmpty, first);
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        if (hasEmptyDerivation()) {
            return super.expand(new Token<TokenProperties>(null, lookAhead.getLine(), lookAhead.getPos(), null));
        } else {
            return super.expand(lookAhead);
        }
    }

    private boolean findEmptyDerivation() {
        for (RuleItemSequence sequence: getOptions()) {
            if (sequence.canBeEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + optionsToString() + ")";
    }

}
