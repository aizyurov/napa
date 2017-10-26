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
    private Tokenizer<TokenProperties> tokenizer;

    public Parser(final CompiledGrammar grammar) throws IOException {
        this.grammar = grammar;
    }

    public Set<SyntaxTreeNode> parse(final String target, final Reader reader, final int complexityLimit) throws IOException {
        this.tokenizer = new DfaTokenizer<>(grammar.getTokenizerDfa(), reader);
        int targetTag = grammar.findNonTerminalByName(target).orElseThrow(() -> new GrammarException("NonTerminal not found: " + grammar));
        workSet.clear();
        shiftCandidates.clear();
        syntaxTreeCandidates.clear();
        final ChartNode startNode = new ChartNode(-1, Collections.singletonList(new NonTerminalItem(targetTag)), 0, null, Collections.emptyList());
        workSet.add(startNode);
        int maxComplexity = 0;
        while (true) {
            int iterations = 0;
            while (!workSet.isEmpty()) {
                iterations += 1;
                if (iterations > complexityLimit) {
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
            maxComplexity = Math.max(iterations, maxComplexity);
            // now shift
            List<String> preface = new ArrayList<>();
            while (true) {
                final Token<TokenProperties> nextToken = tokenizer.nextToken();
                if (nextToken == null) {
                    System.out.println("Max complexity: " + maxComplexity);
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
