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

    private int maxWorkset = 0;
    private int maxComplexity = 0;
    private int totalIterations= 0;
    private int predictCount = 0;
    private int reduceCount = 0;
    private int shiftCount = 0;
    private int expandCount = 0;
    private int tokenCount = 0;
    private int lineCount = 0;
    private long parseTime = 0;

    public List<SyntaxTree> parse(final CompiledGrammar grammar, final String target, final Reader reader) throws IOException {
        return parse(grammar, target, reader, null);
    }

    public List<SyntaxTree> parse(final CompiledGrammar grammar, final String target, final Reader reader, final String filename) throws IOException {
        return parse(grammar, target, reader, filename, new FailFastSyntaxErrorListener());
    }

    public List<SyntaxTree> parse(final CompiledGrammar grammar, final String target, final Reader reader, final String filename, SyntaxErrorListener errorListener) throws IOException {
        return parse(grammar, target, reader, filename, errorListener, 500);
    }

    public List<SyntaxTree> parse(final CompiledGrammar grammar, final String target, final Reader reader, final String filename, SyntaxErrorListener errorListener, final int complexityLimit) throws IOException {
        final Tokenizer<TokenProperties> tokenizer = grammar.createTokenizer(reader);
//        final Tokenizer<TokenProperties> tokenizer = new DfaTokenizer<>(grammar.getTokenizerDfa(), reader);
        final Map<RuleInProgress, NapaChartNode> workSet = new LinkedHashMap<>();
        final Map<RuleInProgress, NapaChartNode> workSetCopy = new LinkedHashMap<>();
        final Map<RuleInProgress, NapaChartNode> shiftCandidates = new HashMap<>();
        final List<RawSyntaxNode> syntaxTreeCandidates = new ArrayList<>();

        final long startTime = System.currentTimeMillis();
        int targetTag = grammar.findNonTerminalByName(target);
        if (targetTag < 0) {
            SyntaxError error = new SyntaxError("NonTerminal not found: " + target, filename, 1, 1);
            if (errorListener != null) {
                errorListener.onError(error);
            }
            return Collections.emptyList();
        }

        List<NapaRule> startRules = grammar.getNapaRules(targetTag);
        workSet.putAll(startRules.stream()
                .map(x -> new NapaChartNode(RuleInProgress.startRule(x, grammar), Collections.emptyList()))
                .collect(Collectors.toMap(NapaChartNode::getRuleInProgress, n -> n)));
        List<Token<TokenProperties>> preface = new ArrayList<>();
        while (true) {
            Token<TokenProperties> nextToken = tokenizer.nextToken();
            while (nextToken.getType() != null && nextToken.getType().isIgnoreOnly()) {
                preface.add(nextToken);
                nextToken = tokenizer.nextToken();
            }
            int iterations = 0;
//            System.out.println("Workset size: " + workSet.size());
//            System.out.println("=== " + nextToken.getText() + " ===");

            workSetCopy.clear();
            workSetCopy.putAll(workSet);
            while (!workSet.isEmpty()) {
                maxWorkset = Math.max(maxWorkset, workSet.size());
//                maxNodes = Math.max(maxNodes, countChartNodes(workSet.values()));
                iterations += 1;
                totalIterations += 1;
                if (iterations == complexityLimit) {
                    SyntaxError error = new SyntaxError("Iterations limit exceeded", filename, 1, 1);
                    if (errorListener != null) {
                        errorListener.onError(error);
                    }
                }
                RuleInProgress nextRule = workSet.keySet().iterator().next();
                NapaChartNode nextNode = workSet.remove(nextRule);
//                System.out.println("-> " + nextRule.toString(grammar));
                List<NapaChartNode> processingResult;
                switch(nextNode.getRuleInProgress().availableAction(nextToken)) {
                    case expand:
                        processingResult = nextNode.expand(nextToken);
                        expandCount += processingResult.size();
//                        for (NapaChartNode node: processingResult) {
//                            System.out.println("expand <- " + node.getRuleInProgress().toString(grammar));
//                        }
                        break;
                    case predict:
                        processingResult = nextNode.predict(nextToken, grammar);
                        predictCount += processingResult.size();
//                        for (NapaChartNode node: processingResult) {
//                            System.out.println("predict <- " + node.getRuleInProgress().toString(grammar));
//                        }
                        break;
                    case reduce:
                        if (nextNode.getEnclosing().isEmpty()) {
                            if (nextToken.getType() == null) {
                                syntaxTreeCandidates.addAll(nextNode.accept(nextToken, grammar));
                            }
                            processingResult = Collections.emptyList();
                        } else {
                            processingResult = nextNode.reduce(nextToken, grammar);
//                            for (NapaChartNode node: processingResult) {
//                                System.out.println("reduce <- " + node.getRuleInProgress().toString(grammar));
//                            }
                            reduceCount += processingResult.size();
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
                lineCount += nextToken.getLine();
                parseTime += (System.currentTimeMillis() - startTime);
                return syntaxTreeCandidates.stream().map(s -> s.toSyntaxTreeNode(null)).collect(Collectors.toList());
            }
            maxComplexity = Math.max(iterations, maxComplexity);
//            System.out.println("=========== Shifting: " + nextToken.getText() + " at " + nextToken.getLine() + ":" + nextToken.getPos());
//            for (ChartNode node: shiftCandidates) {
//                System.out.println(node);
//            }

            if (shiftCandidates.isEmpty()) {
                SyntaxError error = new SyntaxError("Unexpected input \"" + nextToken.getText() + "\"", filename, nextToken.getLine(), nextToken.getPos());
                if (errorListener != null) {
                    errorListener.onError(error);
                }
                preface.add(nextToken);
                workSet.clear();
                workSet.putAll(workSetCopy);
                // skip this token and restart from workSetCopy
            } else {
                ArrayList<Token<TokenProperties>> prefaceCopy = new ArrayList<>(preface);
                for (NapaChartNode candidate : shiftCandidates.values()) {
                    List<NapaChartNode> shifted = candidate.shift(nextToken, prefaceCopy);
                    shiftCount += shifted.size();
                    for (NapaChartNode node : shifted) {
                        RuleInProgress ruleInProgress = node.getRuleInProgress();
                        workSet.put(ruleInProgress, node.merge(workSet.get(ruleInProgress)));
                    }
                }
                tokenCount += 1;
                if (workSet.isEmpty()) {
                    // no node accepted the token
                    // it goes to preface
                    preface.add(nextToken);
                    if (!nextToken.getType().isIgnorable()) {
                        SyntaxError error = new SyntaxError("Unexpected input \"" + nextToken.getText() + "\"", filename, nextToken.getLine(), nextToken.getPos());
                        if (errorListener != null) {
                            errorListener.onError(error);
                        }
                        workSet.clear();
                        workSet.putAll(workSetCopy);
                    }
                } else {
                    // token accepted
                    preface.clear();
                    syntaxTreeCandidates.clear();
                    shiftCandidates.clear();
                }
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

    public Stats stats() {
        return new Stats(maxWorkset, maxComplexity, totalIterations,
                predictCount, reduceCount, shiftCount, expandCount,
                tokenCount, lineCount, parseTime);
    }


    public static class Stats {
        private final int maxWorkset;
        private final int maxComplexity;
        private final int totalIterations;
        private final int predictCount;
        private final int reduceCount;
        private final int shiftCount;
        private final int expandCount;
        private final int tokenCount;
        private final int lineCount;
        private final long parseTime;

        public Stats(int maxWorkset, int maxComplexity, int totalIterations,
                     int predictCount, int reduceCount, int shiftCount,
                     int expandCount, int tokenCount, int lineCount, long parseTime) {
            this.maxWorkset = maxWorkset;
            this.maxComplexity = maxComplexity;
            this.totalIterations = totalIterations;
            this.predictCount = predictCount;
            this.reduceCount = reduceCount;
            this.shiftCount = shiftCount;
            this.expandCount = expandCount;
            this.tokenCount = tokenCount;
            this.lineCount = lineCount;
            this.parseTime = parseTime;
        }

        public int getMaxWorkset() {
            return maxWorkset;
        }

        public int getMaxComplexity() {
            return maxComplexity;
        }

        public int getTotalIterations() {
            return totalIterations;
        }

        public int getTokenCount() {
            return tokenCount;
        }

        public int getLineCount() {
            return lineCount;
        }


        public long getParseTime() {
            return parseTime;
        }

        @Override
        public String toString() {
            return "maxWorkset=" + maxWorkset +
                    "\nmaxComplexity=" + maxComplexity +
                    "\ntotalIterations=" + totalIterations +
                    "\npredictCount=" + predictCount +
                    "\nreduceCount=" + reduceCount +
                    "\nshiftCount=" + shiftCount +
                    "\nexpandCount=" + expandCount +
                    "\ntokenCount=" + tokenCount +
                    "\nlineCount=" + lineCount +
                    "\nparseTime=" + parseTime;
        }
    }








}

