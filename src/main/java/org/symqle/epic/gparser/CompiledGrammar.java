package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.PackedDfa;
import org.symqle.util.TSort;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class CompiledGrammar {

    private final String[] nonTerminals;
    private final String[] terminals;
    private final Map<Integer, List<NapaRule>> napaRules;
    private final PackedDfa<TokenProperties> tokenizerDfa;
    private final Map<Integer, Set<Integer>> firstSets = new HashMap<>();
    private final Set<Integer> haveEmptyDerivation = new HashSet<>();

    public CompiledGrammar(final String[] nonTerminals, final String[] terminals, List<CompiledRule> rules, final PackedDfa<TokenProperties> tokenizerDfa) {
        verify(nonTerminals, rules.stream().map(CompiledRule::getTarget).collect(Collectors.toSet()));
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.tokenizerDfa = tokenizerDfa;
        haveEmptyDerivation.addAll(findEmpty(rules));
        firstSets.putAll(calculateFirstSets(rules));
        this.napaRules = rules.stream().map(x -> x.toNapaRule(this)).collect(Collectors.groupingBy(NapaRule::getTarget));
    }

    private Set<Integer> findEmpty(List<CompiledRule> rules) {
        Set<Integer> known = new HashSet<>();
        for (CompiledRule rule: rules) {
            if (canHaveEmptyDerivation(rule, rules)) {
                known.add(rule.getTarget());
            }

        }
        System.err.println("May be empty: " + known.stream().map(i -> nonTerminals[i]).collect(Collectors.toList()));
        return Collections.unmodifiableSet(known);
    }

    private boolean canHaveEmptyDerivation(CompiledRule rule, List<CompiledRule> allRules) {
        ChartNode0 start = new ChartNode0(rule.getTarget(), rule.getItems(), 0, null,
                allRules.stream().collect(Collectors.groupingBy(CompiledRule::getTarget)));
        Set<ChartNode0> workSet = new HashSet<>();
        workSet.add(start);
        int iterations = 0;
        while (!workSet.isEmpty()) {
            iterations += 1;
            if (iterations > 100) {
                System.err.println("Cannot accept rule: " + start.toString());
                System.err.println("Working set =====");
                for (ChartNode0 node: workSet) {
                    System.err.println(node);
                }
                System.err.println("====");
                throw new GrammarException("Too ambiguous or too complex to parse");
            }
            ChartNode0 next = workSet.iterator().next();
            workSet.remove(next);
            switch (next.availableAction(null)) {
                case SHIFT:
                    break;
                case REDUCE:
                    final List<ChartNode0> reduce = next.reduce(null);
                    //                        for (ChartNode node: reduce) {
                    //                            System.out.println("Reduced: " + node);
                    //                        }
                    workSet.addAll(reduce);
                    break;
                case PREDICT:
                    final List<ChartNode0> predict = next.predict();
                    //                        for (ChartNode node: predict) {
                    //                            System.out.println("Predicted:" + node);
                    //                        }
                    workSet.addAll(predict);
                    break;
                case EXPAND:
                    final List<ChartNode0> expand = next.expand();
                    //                        for (ChartNode node: expand) {
                    //                            System.out.println("Expanded:" + node);
                    //                        }
                    workSet.addAll(expand);
                    break;
                case ACCEPT:
                    return true;
                default:
                    // NONE: do nothing
            }
        }
        return false;
    }

    private boolean mayBeEmpty(List<RuleItem> items, Set<Integer> knownEmpty) {
        for (RuleItem item: items) {
            switch (item.getType()) {
                case TERMINAL:
                    return false;
                case NON_TERMINAL:
                    if (!knownEmpty.contains(item.getValue())) {
                        return false;
                    }
                    break;
                case COMPOUND:
                    boolean definitelyNotEmpty = true;
                    for (List<RuleItem> children: item.expand()) {
                        definitelyNotEmpty &= mayBeEmpty(children, knownEmpty);
                    }
                    if (definitelyNotEmpty) {
                        return false;
                    }
            }
        }
        return true;
    }

    private Map<Integer, Set<Integer>> calculateFirstSets(List<CompiledRule> rules) {
        Map<Integer, Set<Integer>> dependencies = new HashMap<>();
        for (CompiledRule rule: rules) {
            int target = rule.getTarget();
            final Set<Integer> dependencySet = dependencies.getOrDefault(target, new HashSet<>());
            dependencySet.addAll(findFirstNonTerminals(rule.getItems()));
            dependencies.put(target, dependencySet);
        }
        for (int dependent = 0; dependent < nonTerminals.length; dependent ++) {
            dependencies.putIfAbsent(dependent, Collections.emptySet());
        }
        TSort tSort = new TSort(dependencies);
        final List<Integer> sortedNonTerminals;
        try {
            sortedNonTerminals = tSort.sort();
        } catch (TSort.CyclicDependencyException e) {
            final List<String> cycle = e.getCycle().stream().map(this::formatSortedItem).collect(Collectors.toList());
            throw new GrammarException("Cyclic dependency: " + cycle);
        }
        Map<Integer, Set<Integer>> firstSets = new HashMap<>();
        final Map<Integer, List<CompiledRule>> ruleByTarget = rules.stream().collect(Collectors.groupingBy(CompiledRule::getTarget));
        for (Integer key: sortedNonTerminals) {
            Set<Integer> firstSet = new HashSet<>();
            for (CompiledRule rule: ruleByTarget.get(key)) {
                firstSet.addAll(findFirstTerminals(rule.getItems(), Collections.unmodifiableMap(firstSets)));
            }
            firstSets.put(key, firstSet);
        }
        return firstSets;
    }

    private String formatSortedItem(TSort.SortedItem s) {
        StringBuilder builder = new StringBuilder();
        builder.append(nonTerminals[s.getItem()]).append("(");
        for (TSort.SortedItem dep: s.getRightNeighbors()) {
            builder.append(nonTerminals[dep.getItem()]).append(" ");
        }
        builder.append(")");
        return builder.toString();
    }

    private Set<Integer> findFirstTerminals(List<RuleItem> items, Map<Integer, Set<Integer>> knownFirstSets) {
        Set<Integer> first = new HashSet<>();
        for (RuleItem item: items) {
            switch(item.getType()) {
                case TERMINAL:
                    first.add(item.getValue());
                    return first;
                case NON_TERMINAL:
                    first.addAll(knownFirstSets.get(item.getValue()));
                    if (!haveEmptyDerivation.contains(item.getValue())) {
                        return first;
                    }
                    break;
                case COMPOUND:
                    for (List<RuleItem> children: item.expand()) {
                        first.addAll(findFirstTerminals(children, knownFirstSets));
                    }
                    boolean definitelyNotEmpty = true;
                    for (List<RuleItem> children: item.expand()) {
                        definitelyNotEmpty &= mayBeEmpty(children, haveEmptyDerivation);
                    }
                    if (definitelyNotEmpty) {
                        return first;
                    }
            }
        }
        return first;

    }

    private Set<Integer> findFirstNonTerminals(List<RuleItem> items) {
        Set<Integer> first = new HashSet<>();
        for (RuleItem item: items) {
            switch(item.getType()) {
                case TERMINAL:
                    return first;
                case NON_TERMINAL:
                    first.add(item.getValue());
                    if (!haveEmptyDerivation.contains(item.getValue())) {
                        return first;
                    }
                    break;
                case COMPOUND:
                    for (List<RuleItem> children: item.expand()) {
                        first.addAll(findFirstNonTerminals(children));
                    }
                    boolean definitelyNotEmpty = true;
                    for (List<RuleItem> children: item.expand()) {
                        definitelyNotEmpty &= mayBeEmpty(children, haveEmptyDerivation);
                    }
                    if (definitelyNotEmpty) {
                        return first;
                    }
            }
        }
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

    public Set<Integer> getFirstSet(int target) {
        return firstSets.get(target);
    }

    public boolean hasEmptyDerivation(int tag) {
        return haveEmptyDerivation.contains(tag);
    }

    public List<NapaRule> getNapaRules(int target) {
        return napaRules.get(target);
    }
}
