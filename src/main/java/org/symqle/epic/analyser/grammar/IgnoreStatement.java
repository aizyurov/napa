package org.symqle.epic.analyser.grammar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
// ignore_statement = "!" ignored { ignored } ";" ;
// ignored = regexp
public class IgnoreStatement {

    private final List<Ignored> ignoredList = new ArrayList<>();

    public void addIgnored(Ignored ignored) {
        ignoredList.add(ignored);
    }
}
