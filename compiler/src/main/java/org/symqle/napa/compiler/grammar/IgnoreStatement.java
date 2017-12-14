package org.symqle.napa.compiler.grammar;

import java.util.List;

/**
 * @author lvovich
 */
// ignore_statement = "!" STRING { STRING } ";" ;
public class IgnoreStatement {

    private final List<String> ignoredList;

    public IgnoreStatement(final List<String> ignoredList) {
        this.ignoredList = ignoredList;
    }

    public List<String> getIgnoredList() {
        return ignoredList;
    }
}
