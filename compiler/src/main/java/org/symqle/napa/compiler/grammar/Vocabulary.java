package org.symqle.napa.compiler.grammar;

import java.util.Set;

/**
 * @author lvovich
 */
public interface Vocabulary {
    String getTerminalName(int index);

    String getNonTerminalName(int index);

    Set<Integer> getFirstSet(RuleItem ruleItem);

    boolean hasEmptyDerivation(RuleItem item);
}
