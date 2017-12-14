package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.Token;

import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public interface NapaRuleItem {

    RuleItemType getType();

    int getValue();

    String getName();

    List<List<NapaRuleItem>> expand(Token<TokenProperties> lookAhead);

    List<List<NapaRuleItem>> predict(Token<TokenProperties> lookAhead, final CompiledGrammar grammar);

    boolean hasEmptyDerivation();

    Set<Integer> first();

}