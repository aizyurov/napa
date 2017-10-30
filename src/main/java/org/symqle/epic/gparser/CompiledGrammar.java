package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.PackedDfa;
import org.symqle.util.TSort;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class CompiledGrammar {

    private final String[] nonTerminals;
    private final String[] terminals;
    private final Map<Integer, List<CompiledRule>> rules;
    private final PackedDfa<TokenProperties> tokenizerDfa;
    private final Map<Integer, Set<Integer>> firstSets;
    private final Set<Integer> haveEmptyDerivation;

    public CompiledGrammar(final String[] nonTerminals, final String[] terminals, List<CompiledRule> rules, final PackedDfa<TokenProperties> tokenizerDfa) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.rules = rules.stream().collect(Collectors.groupingBy(CompiledRule::getTarget));
        this.tokenizerDfa = tokenizerDfa;
        verify();
        haveEmptyDerivation = findEmpty(rules);
        firstSets = calculateFirstSets(rules);
    }

    private Set<Integer> findEmpty(List<CompiledRule> rules) {
        Set<Integer> known = new HashSet<>();
        Set<Integer> backup = new HashSet<>();
        do {
            backup.addAll(known);
            for (CompiledRule rule: rules) {
                int target = rule.getTarget();
                if (!known.contains(target)) {
                    if (mayBeEmpty(rule.getItems(), known)) {
                        known.add(target);
                    }
                }
            }
        } while (!known.equals(backup));
        return Collections.unmodifiableSet(known);
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
            final List<String> cycle = e.getCycle().stream().map(i -> nonTerminals[i]).collect(Collectors.toList());
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

    private Set<Integer> findFirstTerminals(List<RuleItem> items, Map<Integer, Set<Integer>> knownFirstSets) {
        Set<Integer> first = new HashSet<>();
        for (RuleItem item: items) {
            switch(item.getType()) {
                case TERMINAL:
                    return Collections.singleton(item.getValue());
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



    private void verify() {
        for (int i = 0; i < nonTerminals.length; i++) {
            if (!rules.containsKey(i)) {
                throw new GrammarException("No rule for " + nonTerminals[i]);
            }
        }
    }



    public List<CompiledRule> getRules(int index) {
        return Collections.unmodifiableList(rules.get(index));
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
}
