package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.PackedDfa;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class CompiledGrammar {

    private final String[] nonTerminals;
    private final String[] terminals;
    private final Map<Integer, List<NapaRule>> napaRules;
    private final PackedDfa<TokenProperties> tokenizerDfa;
    private final Map<RuleItem, Set<Integer>> firstSets = new HashMap<>();
    private final Set<RuleItem> haveEmptyDerivation = new HashSet<>();

    public CompiledGrammar(final String[] nonTerminals, final String[] terminals, List<CompiledRule> rules, final PackedDfa<TokenProperties> tokenizerDfa) {
        verify(nonTerminals, rules.stream().map(CompiledRule::getTarget).collect(Collectors.toSet()));
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.tokenizerDfa = tokenizerDfa;
        Map<Integer, List<CompiledRule>> ruleMap = rules.stream().collect(Collectors.groupingBy(CompiledRule::getTarget));
        Set<RuleItem> allItems = new HashSet<>();
        for (CompiledRule rule: rules) {
            addItems(allItems, Collections.singletonList(new NonTerminalItem(rule.getTarget())));
            addItems(allItems, rule.getItems());
        }
        long startTs = System.currentTimeMillis();
        AtomicInteger maxIterations = new AtomicInteger();
        for (RuleItem item: allItems) {
            Set<Integer> first = analyze(item, ruleMap, maxIterations);
            if (first.remove(-1)) {
                haveEmptyDerivation.add(item);
            }
            firstSets.put(item, first);

        }
        System.err.println("Analyser: " + (System.currentTimeMillis() - startTs) );
        System.err.println("Max iterations: " + maxIterations.get());
        this.napaRules = rules.stream().map(x -> x.toNapaRule(this)).collect(Collectors.groupingBy(NapaRule::getTarget));
    }

    private void addItems(Set<RuleItem> allITems, List<RuleItem> items) {
        for (RuleItem item: items) {
            if (allITems.contains(item)) {
                continue;
            }
            allITems.add(item);
            addItems(allITems, item.expand().stream().flatMap(List::stream).collect(Collectors.toList()));
        }
    }

    private Set<Integer> analyze(RuleItem item, Map<Integer, List<CompiledRule>> ruleMap, AtomicInteger maxIterations) {
        RuleInProgress0 startRule = new RuleInProgress0(-1, Collections.singletonList(item), 0, ruleMap);
        ChartNode0 startNode = new ChartNode0(startRule, null);
        final Map<RuleInProgress0, ChartNode0> workSet = new LinkedHashMap<>();
        final Map<RuleInProgress0, ChartNode0> shiftCandidates = new HashMap<>();
        final Map<RuleInProgress0, Integer> ruleCounts = new HashMap<>();
        final Set<RuleInProgress0> knownRules = new HashSet<>();
        workSet.put(startRule, startNode);
        int iterations = 0;
        List<ChartNode0> accepted = new ArrayList<>();
        while (!workSet.isEmpty()) {
            iterations += 1;
            if (iterations > 1000) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Too ambiguous or too complex: ").append(startRule.toString(this))
                        .append("\n");
                stringBuilder.append("Stop at\n");
                System.err.println("Cannot process rule: " + startRule.toString(this));
                System.err.println("Working set =====");
                for (ChartNode0 node: workSet.values()) {
                    stringBuilder.append("    ").append(node.format(this));
                    System.err.println(node.format(this));
                }
                System.err.println("====");
                throw new GrammarException("Too ambiguous or too complex to parse: " + stringBuilder);
            }
            RuleInProgress0 nextRule = workSet.keySet().iterator().next();
            ChartNode0 next = workSet.remove(nextRule);
//            System.out.println("<<< " + next.format(this));
            if (!knownRules.add(nextRule)) {
                next.infiniteRecursionCheck(this);
            }
            switch (next.availableAction()) {
                case SHIFT:
                    shiftCandidates.put(nextRule, next.merge(workSet.get(nextRule)));
                    break;
                case REDUCE:
                    ruleCounts.put(nextRule, ruleCounts.getOrDefault(nextRule, 0) + 1);
                    final List<ChartNode0> reduce = next.reduce();
//                                            for (ChartNode0 node: reduce) {
//                                                System.out.println("Reduced: " + node.format(this));
//                                            }
                    for (ChartNode0 node: reduce) {
                        RuleInProgress0 ruleInProgress = node.getRuleInProgress();
                        workSet.put(ruleInProgress, node.merge(workSet.get(ruleInProgress)));
                    }
                    break;
                case PREDICT:
                    ruleCounts.put(nextRule, ruleCounts.getOrDefault(nextRule, 0) + 1);
                    final List<ChartNode0> predict = next.predict();
//                                            for (ChartNode0 node: predict) {
//                                                System.out.println("Predicted:" + node.format(this));
//                                            }
                    for (ChartNode0 node: predict) {
                        RuleInProgress0 ruleInProgress = node.getRuleInProgress();
                        workSet.put(ruleInProgress, node.merge(workSet.get(ruleInProgress)));
                    }
                    break;
                case EXPAND:
                    ruleCounts.put(nextRule, ruleCounts.getOrDefault(nextRule, 0) + 1);
                    final List<ChartNode0> expand = next.expand();
//                                            for (ChartNode0 node: expand) {
//                                                System.out.println("Expanded:" + node.format(this));
//                                            }
                    for (ChartNode0 node: expand) {
                        RuleInProgress0 ruleInProgress = node.getRuleInProgress();
                        workSet.put(ruleInProgress, node.merge(workSet.get(ruleInProgress)));
                    }
                    break;
                case ACCEPT:
                    accepted.addAll(next.accept());
                default:
                    // NONE: do nothing
            }
        }
//        System.out.println("DONE " + nonTerminals[target] + ", first: " + shiftCandidates.keySet().stream().map(x -> x.toString(this)).collect(Collectors.toList()));
        HashSet<Integer> first = shiftCandidates.keySet().stream().map(c -> c.getItems().get(c.getOffset()).getValue()).collect(Collectors.toCollection(HashSet::new));
        if (!accepted.isEmpty()) {
            first.add(-1);
        }
//        int maxCount = 0;
//        RuleInProgress0 maxRule = null;
//        for (RuleInProgress0 key: ruleCounts.keySet()) {
//            Integer count = ruleCounts.get(key);
//            if (count > maxCount) {
//                maxCount = count;
//                maxRule = key;
//            }
//        }
//        if (maxRule != null && maxCount > 1) {
//            System.err.println("Max duplicates: " + maxCount + " - " + maxRule.toString(this));
//        }
        maxIterations.set(Math.max(maxIterations.get(), iterations));
        return first;

    }

    private void verify(String[] nonTerminals, Set<Integer> targets) {
        for (int i = 0; i < nonTerminals.length; i++) {
            if (!targets.contains(i)) {
                throw new GrammarException("No rule for " + nonTerminals[i]);
            }
        }
    }



    public String getTerminalName(int index) {
        return terminals[index];
    }

    public String getNonTerminalName(int index) {
        return nonTerminals[index];
    }

    public Optional<Integer> findNonTerminalByName(String name) {
        for (int i = 0; i < nonTerminals.length; i++ ) {
            if (nonTerminals[i].equals(name)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    public PackedDfa<TokenProperties> getTokenizerDfa() {
        return tokenizerDfa;
    }

    public Set<Integer> getFirstSet(RuleItem ruleItem) {
        return firstSets.getOrDefault(ruleItem, Collections.emptySet());
    }

    public boolean hasEmptyDerivation(RuleItem item) {
        return haveEmptyDerivation.contains(item);
    }

    public List<NapaRule> getNapaRules(int target) {
        return napaRules.get(target);
    }
}
