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
//        this.tokenizer = new AsyncTokenizer<>(new DfaTokenizer<>(grammar.getTokenizerDfa(), reader));
        this.tokenizer = new DfaTokenizer<>(grammar.getTokenizerDfa(), reader);
        final long startTime = System.currentTimeMillis();
        int targetTag = grammar.findNonTerminalByName(target).orElseThrow(() -> new GrammarException("NonTerminal not found: " + target));
        workSet.clear();
        shiftCandidates.clear();
        syntaxTreeCandidates.clear();
        RuleInProgress ruleInProgress = new RuleInProgress(-1, Collections.singletonList(new NapaNonTerminalItem(targetTag, grammar)), 0, Collections.emptyList(), grammar);
        final NapaChartNode startNode = new NapaChartNode(ruleInProgress, Collections.emptySet());
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
            System.out.println("Workset size: " + workSet.size());
            while (!workSet.isEmpty()) {
                iterations += 1;
                if (iterations > complexityLimit) {
                    throw new GrammarException("Too ambiguous or too complex to parse");
                }
                RuleInProgress nextRule = workSet.keySet().iterator().next();
                NapaChartNode nextNode = workSet.get(nextRule);
                ProcessingResult result = nextNode.process(nextToken);
                List<NapaChartNode> same = new ArrayList<>();
                workSet.remove(nextRule);
                for (NapaChartNode node: result.getNoShift()) {
                    if (node.getRuleInProgress().equals(nextRule)) {
                        same.add(node);
                    } else {
                        NapaChartNode previous = workSet.get(node.getRuleInProgress());
                        if (previous == null) {
                            workSet.put(node.getRuleInProgress(), node);
                        } else {
                            for (NapaChartNode merged: node.merge(previous)) {
                                workSet.put(merged.getRuleInProgress(), merged);
                            }
                        }
                    }
                }
                if (!same.isEmpty()) {
                    throw new UnsupportedOperationException("Not implemented");
                }

                for (NapaChartNode node: result.getShiftCandidates()) {
                    NapaChartNode previous = shiftCandidates.get(node.getRuleInProgress());
                    if (previous != null) {
                        for (NapaChartNode merged: node.merge(previous)) {
                            shiftCandidates.put(merged.getRuleInProgress(), merged);
                        }
                    } else {
                        shiftCandidates.put(node.getRuleInProgress(), node);
                    }
                }

                syntaxTreeCandidates.addAll(result.getAccepted());
//                System.out.println("Work set size: " + workSet.size());
//                for (ChartNode node: workSet) {
//                    System.out.println(node);
//                }
            }
            if (nextToken == null) {
                System.out.println("Max complexity: " + maxComplexity);
                System.out.println("Parse time: " + (System.currentTimeMillis() - startTime));
                return syntaxTreeCandidates.stream().map(s -> s.toSyntaxTreeNode(null, grammar)).collect(Collectors.toList());
            }
            maxComplexity = Math.max(iterations, maxComplexity);
            System.out.println("=========== Shifting: " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
//            for (ChartNode node: shiftCandidates) {
//                System.out.println(node);
//            }

            if (shiftCandidates.isEmpty()) {
                // no node can shift
                throw new GrammarException("Unrecognized input " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
            }
            ArrayList<Token<TokenProperties>> prefaceCopy = new ArrayList<>(preface);
            for (NapaChartNode candidate : shiftCandidates.values()) {
                List<NapaChartNode> shifted = candidate.shift(nextToken, prefaceCopy);
                throw new UnsupportedOperationException("Not implemented");
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



    private final Map<RuleInProgress, NapaChartNode> workSet = new HashMap<>();

    private final Map<RuleInProgress, NapaChartNode> shiftCandidates = new HashMap<>()

    private final Set<RawSyntaxNode> syntaxTreeCandidates = new HashSet<>();








}

/*
                    System.out.println("Max complexity: " + maxComplexity);
                    System.out.println("Parse time: " + (System.currentTimeMillis() - startTime));

 */