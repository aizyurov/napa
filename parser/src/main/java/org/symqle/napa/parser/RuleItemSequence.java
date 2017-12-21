package org.symqle.napa.parser;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aizyurov on 11/23/17.
 */
public class RuleItemSequence {

    private final List<NapaRuleItem> sequence;

    public RuleItemSequence(List<NapaRuleItem> sequence) {
        this.sequence = sequence;
    }

    public List<NapaRuleItem> getSequence() {
        return sequence;
    }

    boolean canBeEmpty() {
        if (canBeEmpty != null) {
            return canBeEmpty;
        } else {
            boolean result = true;
            for (NapaRuleItem item: sequence) {
                if (!item.hasEmptyDerivation()) {
                    result = false;
                    break;
                }
            }
            canBeEmpty = result;
            return result;
        }
    }

    public BitSet firstSet() {
        if (firstSet != null) {
            return firstSet;
        }
        BitSet result = new BitSet();
        for (NapaRuleItem item: sequence) {
            result.or(item.first());
            if (!item.hasEmptyDerivation()) {
                break;
            }
        }
        firstSet = result;
        return result;
    }

    private Boolean canBeEmpty;

    private BitSet firstSet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RuleItemSequence that = (RuleItemSequence) o;

        return sequence.equals(that.sequence);

    }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            h = sequence.hashCode();
            hash = h;
        }
        return h;
    }

    private int hash;
}
