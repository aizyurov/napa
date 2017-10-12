package org.symqle.epic.analyser.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class Dictionary {

    private Map<String, Integer> regexpList = new HashMap<>();
    private Map<String, Integer> nonTerminals = new HashMap<>();

    public Integer registerRegexp(String regexp) {
        Integer index = regexpList.getOrDefault(regexp, regexpList.size() + 1);
        regexpList.put(regexp, index);
        return index;
    }

    public Integer registerNonTerminal(String name) {
        Integer index = nonTerminals.getOrDefault(name, nonTerminals.size() + 1);
        nonTerminals.put(name, index);
        return index;
    }

    public List<String> nonTerminals() {
        List<String> nonTerminalNames = new ArrayList<>(nonTerminals.size());
        for (Map.Entry<String, Integer> entry : nonTerminals.entrySet()) {
            nonTerminalNames.set(entry.getValue(), entry.getKey());
        }
        return nonTerminalNames;
    }

    public List<String> terminals() {
        List<String> patterns = new ArrayList<>(regexpList.size());
        for (Map.Entry<String, Integer> entry : regexpList.entrySet()) {
            patterns.set(entry.getValue(), entry.getKey());
        }
        return patterns;
    }
}
