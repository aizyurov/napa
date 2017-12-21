package org.symqle.napa.compiler.grammar;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvovich
 */
public class Dictionary {

    private Map<String, Integer> regexpList = new HashMap<>();
    private Map<String, Integer> nonTerminals = new HashMap<>();
    private Map<String, String> patterns = new HashMap<>();

    public Integer registerRegexp(String regexp) {
        Integer index = regexpList.getOrDefault(regexp, regexpList.size());
        regexpList.put(regexp, index);
        return index;
    }

    public Integer registerNonTerminal(String name) {
        Integer index = nonTerminals.getOrDefault(name, nonTerminals.size());
        nonTerminals.put(name, index);
        return index;
    }

    public String registerPattern(String name, String value) {
        return patterns.put(name, value);
    }

    public String[] nonTerminals() {
        String[] nonTerminalNames = new String[nonTerminals.size()];
        for (Map.Entry<String, Integer> entry : nonTerminals.entrySet()) {
            nonTerminalNames[entry.getValue()] = entry.getKey();
        }
        return nonTerminalNames;
    }

    public String[] terminals() {
        String[] patterns = new String[regexpList.size()];
        for (Map.Entry<String, Integer> entry : regexpList.entrySet()) {
            patterns[entry.getValue()] = entry.getKey();
        }
        return patterns;
    }

    public String pattern(String name) {
        return patterns.get(name);
    }
}
