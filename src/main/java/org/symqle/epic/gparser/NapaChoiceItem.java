package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.List;

/**
 * @author lvovich
 */
public class NapaChoiceItem extends AbstractNapaCompoundItem {

    private final boolean hasEmptyDerivation;

    public NapaChoiceItem(final List<List<NapaRuleItem>> options) {
        super(options);
        hasEmptyDerivation = findEmptyDerivation();
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
        for (List<NapaRuleItem> option: expand(null)) {
            boolean hasEmptyDerivation = true;
            for (NapaRuleItem item: option) {
                if (!item.hasEmptyDerivation()) {
                    hasEmptyDerivation = false;
                    break;
                }
            }
            if (hasEmptyDerivation) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasEmptyDerivation() {
        return hasEmptyDerivation;
    }

    @Override
    public String toString() {
        return "(" + optionsToString() + ")";
    }
}
