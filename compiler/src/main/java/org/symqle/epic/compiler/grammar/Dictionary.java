package org.symqle.epic.compiler.grammar;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvovich
 */
public class Dictionary {

    private Map<String, Integer> regexpList = new HashMap<>();
    private Map<String, Integer> nonTerminals = new HashMap<>();

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
}
