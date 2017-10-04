package org.symqle.epic.gparser;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lvovich
 */
public class Parser {

    private final CompiledGrammar grammar;
    private final ParserTokenizer tokenizer;

    public Parser(final CompiledGrammar grammar, final ParserTokenizer tokenizer) {
        this.grammar = grammar;
        this.tokenizer = tokenizer;
    }

    private final Set<ChartNode> workSet = new HashSet<>();

    private final Set<ChartNode> shiftCandidates = new HashSet<>();



}
