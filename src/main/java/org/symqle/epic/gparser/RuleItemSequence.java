package org.symqle.epic.gparser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<Integer> firstSet() {
        if (firstSet != null) {
            return firstSet;
        }
        Set<Integer> result = new HashSet<>();
        for (NapaRuleItem item: sequence) {
            result.addAll(item.first());
            if (!item.hasEmptyDerivation()) {
                break;
            }
        }
        firstSet = result;
        return result;
    }

    private Boolean canBeEmpty;

    private Set<Integer> firstSet;
}
