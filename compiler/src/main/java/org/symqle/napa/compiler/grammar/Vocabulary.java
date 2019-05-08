package org.symqle.napa.compiler.grammar;

import java.util.Set;

/**
 * @author lvovich
 */
public interface Vocabulary {

    String getNonTerminalName(int index);

    Set<Integer> getFirstSet(RuleItem ruleItem);

    boolean hasEmptyDerivation(RuleItem item);
}
