package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.List;

/**
 * @author lvovich
 */
public class NapaChoiceItem extends AbstractNapaCompoundItem {

    private Boolean hasEmptyDerivation;

    public NapaChoiceItem(final List<List<NapaRuleItem>> options) {
        super(options);
    }

    @Override
    public List<List<NapaRuleItem>> expand(final Token<TokenProperties> lookAhead) {
        if (hasEmptyDerivation()) {
            return super.expand(null);
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
    public boolean hasEmptyDerivation() {
        if (hasEmptyDerivation == null) {
            hasEmptyDerivation = findEmptyDerivation();
        }
        return hasEmptyDerivation;
    }

    @Override
    public String toString() {
        return "(" + optionsToString() + ")";
    }

}
