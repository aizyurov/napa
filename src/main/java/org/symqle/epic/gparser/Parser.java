package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.DfaTokenizer;
import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class Parser {

    private final CompiledGrammar grammar;
    private final Tokenizer<TokenProperties> tokenizer;
    private final int target;
    private final int complexityLimit;

    public Parser(final CompiledGrammar grammar, final String target, final Reader reader, final int complexityLimit) throws IOException {
        this.grammar = grammar;
        this.tokenizer = new DfaTokenizer<>(grammar.getTokenizerDfa(), reader);
        this.target = grammar.findNonTerminalByName(target).orElseThrow(() -> new GrammarException("NonTerminal not found: " + grammar));
        this.complexityLimit = complexityLimit;
    }

    public Set<SyntaxTreeNode> parse() throws IOException {
        final ChartNode startNode = new ChartNode(-1, Collections.singletonList(new NonTerminalItem(target)), 0, null, Collections.emptyList());
        workSet.add(startNode);
        while (true) {
            while (!workSet.isEmpty()) {
                if (workSet.size() > complexityLimit) {
                    throw new GrammarException("Too ambiguous or too complex to parse");
                }
                final ChartNode next = workSet.iterator().next();
                workSet.remove(next);
                switch (next.availableAction()) {
                    case SHIFT:
                        shiftCandidates.add(next);
                        break;
                    case REDUCE:
                        workSet.addAll(next.reduce(grammar));
                        break;
                    case PREDICT:
                        workSet.addAll(next.predict(grammar));
                        break;
                    case EXPAND:
                        workSet.addAll(next.expand());
                        break;
                    case ACCEPT:
                        syntaxTreeCandidates.add(next.accept());
                        break;
                    default:
                        throw new IllegalStateException("Should never get here");
                }
            }
            // now shift
            List<String> preface = new ArrayList<>();
            while (true) {
                final Token<TokenProperties> nextToken = tokenizer.nextToken();
                if (nextToken == null) {
                    return syntaxTreeCandidates;
                }
                if (nextToken.getType().isIgnoreOnly()) {
                    preface.add(nextToken.getText());
                    continue;
                }
                for (ChartNode candidate: shiftCandidates) {
                    workSet.addAll(candidate.shift(nextToken, preface, grammar));
                }
                if (workSet.isEmpty()) {
                    if (nextToken.getType().isIgnorable()) {
                        preface.add(nextToken.getText());
                    } else {
                        throw new GrammarException("Unrecognized input " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
                    }
                } else {
                    break;
                }
            }
            // token accepted
            syntaxTreeCandidates.clear();
            shiftCandidates.clear();
        }

    }

    private final Set<ChartNode> workSet = new HashSet<>();

    private final Set<ChartNode> shiftCandidates = new HashSet<>();

    private final Set<SyntaxTreeNode> syntaxTreeCandidates = new HashSet<>();








}
