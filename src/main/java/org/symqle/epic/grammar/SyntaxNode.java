package org.symqle.epic.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class SyntaxNode implements SyntaxTree {

    private final String name;
    private final List<SyntaxTree> children = new ArrayList<>();

    public SyntaxNode(final String name) {
        this.name = name;
    }

    public void addChild(final SyntaxTree child) {
        this.children.add(child);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public List<SyntaxTree> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String value() {
        return String.join(" ", children.stream().map(x -> x.value()).collect(Collectors.<String>toList()));
    }
}
