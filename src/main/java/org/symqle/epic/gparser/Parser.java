package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.DfaTokenizer;
import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class Parser {

    private final CompiledGrammar grammar;
    private Tokenizer<TokenProperties> tokenizer;

    public Parser(final CompiledGrammar grammar) throws IOException {
        this.grammar = grammar;
    }

    public List<SyntaxTree> parse(final String target, final Reader reader, final int complexityLimit) throws IOException {
        final long startTime = System.currentTimeMillis();
        this.tokenizer = new DfaTokenizer<>(grammar.getTokenizerDfa(), reader);
        int targetTag = grammar.findNonTerminalByName(target).orElseThrow(() -> new GrammarException("NonTerminal not found: " + grammar));
        workSet.clear();
        shiftCandidates.clear();
        syntaxTreeCandidates.clear();
        final ChartNode startNode = new ChartNode(-1, Collections.singletonList(new NonTerminalItem(targetTag)), 0, null, Collections.emptyList(), grammar);
        workSet.add(startNode);
        int maxComplexity = 0;
        List<Token<TokenProperties>> preface = new ArrayList<>();
        while (true) {
            Token<TokenProperties> nextToken = tokenizer.nextToken();
            while (nextToken != null && nextToken.getType().isIgnoreOnly()) {
                preface.add(nextToken);
                nextToken = tokenizer.nextToken();
            }
            int iterations = 0;
            while (!workSet.isEmpty()) {
                iterations += 1;
                if (iterations > complexityLimit) {
                    throw new GrammarException("Too ambiguous or too complex to parse");
                }
                final ChartNode next = workSet.iterator().next();
                workSet.remove(next);
                switch (next.availableAction(nextToken)) {
                    case SHIFT:
                        shiftCandidates.add(next);
                        break;
                    case REDUCE:
                        final List<ChartNode> reduce = next.reduce(nextToken);
//                        for (ChartNode node: reduce) {
//                            System.out.println(node);
//                        }
                        workSet.addAll(reduce);
                        break;
                    case PREDICT:
                        final List<ChartNode> predict = next.predict();
//                        for (ChartNode node: predict) {
//                            System.out.println(node);
//                        }
                        workSet.addAll(predict);
                        break;
                    case EXPAND:
                        final List<ChartNode> expand = next.expand();
//                        for (ChartNode node: expand) {
//                            System.out.println(node);
//                        }
                        workSet.addAll(expand);
                        break;
                    case ACCEPT:
                        syntaxTreeCandidates.add(next.accept());
                        break;
                    default:
                        // NONE: do nothing
                }
            }
            if (nextToken == null) {
                System.out.println("Max complexity: " + maxComplexity);
                System.out.println("Parse time: " + (System.currentTimeMillis() - startTime));
                return syntaxTreeCandidates.stream().map(s -> s.toSyntaxTreeNode(null, grammar)).collect(Collectors.toList());
            }
            maxComplexity = Math.max(iterations, maxComplexity);
            // now shift
//            System.out.println("=========== Total nodes:" + countChartNodes(shiftCandidates));
            if (shiftCandidates.isEmpty()) {
                // no node can shift
                throw new GrammarException("Unrecognized input " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
            }
            ArrayList<Token<TokenProperties>> prefaceCopy = new ArrayList<>(preface);
            for (ChartNode candidate : shiftCandidates) {
                workSet.addAll(candidate.shift(nextToken, prefaceCopy));
            }
            if (workSet.isEmpty()) {
                // no node accepted the token
                if (nextToken.getType().isIgnorable()) {
                    preface.add(nextToken);
                } else {
                    throw new GrammarException("Unexpected input: " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
                }
            } else {
                // token accepted
                preface.clear();
                syntaxTreeCandidates.clear();
                shiftCandidates.clear();
            }
        }
    }

    int countChartNodes(Collection<ChartNode> start) {
        Set<ChartNode> toSearch = new HashSet<>(start);
        Set<ChartNode> result = new HashSet<>();
        while (!toSearch.isEmpty()) {
            final ChartNode next = toSearch.iterator().next();
            result.add(next);
            toSearch.remove(next);
            final ChartNode enclosing = next.getEnclosing();
            if (enclosing != null && !result.contains(enclosing)) {
                toSearch.add(enclosing);
            }
        }
        return result.size();
    }



    private final Set<ChartNode> workSet = new HashSet<>();

    private final Set<ChartNode> shiftCandidates = new HashSet<>();

    private final Set<RawSyntaxNode> syntaxTreeCandidates = new HashSet<>();








}

/*
                    System.out.println("Max complexity: " + maxComplexity);
                    System.out.println("Parse time: " + (System.currentTimeMillis() - startTime));

 */