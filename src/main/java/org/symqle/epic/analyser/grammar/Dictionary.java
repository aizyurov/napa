package org.symqle.epic.analyser.grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class Dictionary {

    private Map<String, Integer> regexpList = new HashMap<>();
    private Set<String> ignoredSet = new HashSet<>();
    private Map<String, Integer> nonTerminals = new HashMap<>();

    public Integer registerRegexp(String regexp) {
        Integer index = regexpList.getOrDefault(regexp, regexpList.size() + 1);
        regexpList.put(regexp, index);
        return index;
    }

    public void registerIgnored(String ignored) {
        ignoredSet.add(ignored);
    }

    public Integer registerNonTerminal(String name) {
        Integer index = nonTerminals.getOrDefault(name, nonTerminals.size() + 1);
        nonTerminals.put(name, index);
        return index;
    }
}
