package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.AsyncTokenizer;
import org.symqle.napa.tokenizer.DfaTokenizer;
import org.symqle.napa.tokenizer.Token;
import org.symqle.napa.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class Parser {

    private final CompiledGrammar compiledGrammar;

    public Parser(final CompiledGrammar compiledGrammar) throws IOException {
        this.compiledGrammar = compiledGrammar;
    }

    public List<SyntaxTree> parse(final String target, final Reader reader, final int complexityLimit) throws IOException {
        final CompiledGrammar grammar = this.compiledGrammar;
        final Tokenizer<TokenProperties> tokenizer = new AsyncTokenizer<>(new DfaTokenizer<>(grammar.getTokenizerDfa(), reader));
//        final Tokenizer<TokenProperties> tokenizer = new DfaTokenizer<>(grammar.getTokenizerDfa(), reader);
        final Map<RuleInProgress, NapaChartNode> workSet = new LinkedHashMap<>();
        final Map<RuleInProgress, NapaChartNode> shiftCandidates = new HashMap<>();
        final List<RawSyntaxNode> syntaxTreeCandidates = new ArrayList<>();

        final long startTime = System.currentTimeMillis();
        int targetTag = grammar.findNonTerminalByName(target);
        if (targetTag < 0) {
            throw new GrammarException("NonTerminal not found: " + target);
        }

        List<NapaRule> startRules = grammar.getNapaRules(targetTag);
        workSet.putAll(startRules.stream()
                .map(x -> new NapaChartNode(RuleInProgress.startRule(x, grammar), Collections.emptyList()))
                .collect(Collectors.toMap(NapaChartNode::getRuleInProgress, n -> n)));
        int maxComplexity = 0;
        List<Token<TokenProperties>> preface = new ArrayList<>();
        int maxNodes = 0;
        int maxWorkset = 0;
        int totalIterations= 0;
        int tokenCount = 0;
        while (true) {
            Token<TokenProperties> nextToken = tokenizer.nextToken();
            while (nextToken.getType() != null && nextToken.getType().isIgnoreOnly()) {
                preface.add(nextToken);
                nextToken = tokenizer.nextToken();
            }
            int iterations = 0;
//            System.out.println("Workset size: " + workSet.size());
            while (!workSet.isEmpty()) {
                maxWorkset = Math.max(maxWorkset, workSet.size());
//                maxNodes = Math.max(maxNodes, countChartNodes(workSet.values()));
                iterations += 1;
                totalIterations += 1;
                if (iterations == complexityLimit) {
                    System.out.println("Complexity limit reached");
                        syntaxTreeCandidates.stream().map(x -> x.toSyntaxTreeNode(null)).forEach(x -> {
                            try {
                                x.print(System.out);
                            } catch (IOException e) {
                                throw new RuntimeException("TODO: handle me", e);
                            }
                        });
                    throw new GrammarException("bebe");
                }
                RuleInProgress nextRule = workSet.keySet().iterator().next();
                NapaChartNode nextNode = workSet.remove(nextRule);
                List<NapaChartNode> processingResult;
                switch(nextNode.getRuleInProgress().availableAction(nextToken)) {
                    case expand:
                        processingResult = nextNode.expand(nextToken);
                        break;
                    case predict:
                        processingResult = nextNode.predict(nextToken, grammar);
                        break;
                    case reduce:
                        if (nextNode.getEnclosing().isEmpty()) {
                            if (nextToken.getType() == null) {
                                syntaxTreeCandidates.addAll(nextNode.accept(nextToken, grammar));
                            }
                            processingResult = Collections.emptyList();
                        } else {
                            processingResult = nextNode.reduce(nextToken, grammar);
                        }
                        break;
                    case shift:
                        processingResult = Collections.singletonList(nextNode);
                        break;
                    default:
                        // do nothing
                        processingResult = Collections.emptyList();
                }
//                System.out.println("<<< " + nextNode);
                // sort nodes
                List<NapaChartNode> forWorkSet = new ArrayList<>();
                List<NapaChartNode> forShiftCandidates = new ArrayList<>();
                for (NapaChartNode node: processingResult) {
                    final RuleInProgress.Action action = node.getRuleInProgress().availableAction(nextToken);
                    if (action == RuleInProgress.Action.shift) {
                        forShiftCandidates.add(node);
                    } else if (action != RuleInProgress.Action.none) {
                        forWorkSet.add(node);
                    }
                }
                for (NapaChartNode node: forWorkSet) {
                    RuleInProgress ruleInProgress = node.getRuleInProgress();
                    final NapaChartNode existing = workSet.get(ruleInProgress);
                    workSet.put(ruleInProgress,
                                node.merge(existing));
//                    System.out.println(">>> " + ruleInProgress);
                }
                for (NapaChartNode node: forShiftCandidates) {
                    RuleInProgress ruleInProgress = node.getRuleInProgress();
                    shiftCandidates.put(ruleInProgress,
                            node.merge(shiftCandidates.get(ruleInProgress)));
//                    System.out.println("=== " + ruleInProgress);
                }

//                for (ChartNode node: workSet) {
//                    System.out.println(node);
//                }
            }
//            System.out.println("Iterations: " + iterations);
            if (nextToken.getType() == null) {
                System.out.println("Max complexity: " + maxComplexity);
                System.out.println("Max workset: " + maxWorkset);
                System.out.println("Max nodes: " + maxNodes);
                System.out.println("Total iterations: " + totalIterations);
                System.out.println("Tokens: " + tokenCount);
                System.out.println("Parse time: " + (System.currentTimeMillis() - startTime));
                return syntaxTreeCandidates.stream().map(s -> s.toSyntaxTreeNode(null)).collect(Collectors.toList());
            }
            maxComplexity = Math.max(iterations, maxComplexity);
//            System.out.println("=========== Shifting: " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
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
                for (NapaChartNode node: shifted) {
                    RuleInProgress ruleInProgress = node.getRuleInProgress();
                    workSet.put(ruleInProgress, node.merge(workSet.get(ruleInProgress)));
                }
            }
            tokenCount += 1;
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

    int countChartNodes(Collection<NapaChartNode> start) {
        Set<NapaChartNode> toSearch = new HashSet<>(start);
        Set<NapaChartNode> result = new HashSet<>();
        while (!toSearch.isEmpty()) {
            final NapaChartNode next = toSearch.iterator().next();
            result.add(next);
            toSearch.remove(next);
            final Set<NapaChartNode> enclosing = new HashSet<>(next.getEnclosing());
            enclosing.removeAll(result);
            toSearch.addAll(enclosing);
        }
        return result.size();
    }











}
