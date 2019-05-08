package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.AsyncTokenizer;
import org.symqle.napa.tokenizer.DfaTokenizer;
import org.symqle.napa.tokenizer.PackedDfa;
import org.symqle.napa.tokenizer.Tokenizer;

import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class CompiledGrammar {

    private final Map<Integer, List<NapaRule>> napaRules;
    private final PackedDfa<TokenProperties> tokenizerDfa;
    private final String[] nonTerminals;

    public CompiledGrammar(final Map<Integer, List<NapaRule>> napaRules, final PackedDfa<TokenProperties> tokenizerDfa, String[] nonTerminals) {
        this.napaRules = napaRules;
        this.tokenizerDfa = tokenizerDfa;
        this.nonTerminals = nonTerminals;
    }

    public Tokenizer<TokenProperties> createTokenizer(Reader reader) {
        return new AsyncTokenizer<>(new DfaTokenizer<>(tokenizerDfa, reader, new TokenProperties(false, false, Collections.emptySet())));
    }

    public List<NapaRule> getNapaRules(int target) {
        return napaRules.get(target);
    }

    public int findNonTerminalByName(String name) {
        for (int i = 0; i < nonTerminals.length; i++) {
            if (nonTerminals[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public String nonTerminalName(int tag) {
        return nonTerminals[tag];
    }
}
