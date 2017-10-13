package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class NonTerminalNode implements SyntaxTreeNode {

    private final String name;
    // at least one element: we do not allow empty productions
    private final List<SyntaxTreeNode> children;

    public NonTerminalNode(String name, final List<SyntaxTreeNode> children) {
        this.name = name;
        if (children.isEmpty()) {
            throw new IllegalStateException("NonTerminal cannot be expanded to empty sequence");
        }
        this.children = new ArrayList<>(children);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return children.get(0).value();
    }

    @Override
    public List<String> preface() {
        return children.get(0).preface();
    }

    @Override
    public String text() {
        return children.stream().map(SyntaxTreeNode::text).reduce("", (a, b) -> a + b);
    }

    @Override
    public String file() {
        return children.get(0).file();
    }

    @Override
    public int line() {
        return children.get(0).line();
    }

    @Override
    public int pos() {
        return children.get(0).pos();
    }

    @Override
    public boolean isTermimal() {
        return false;
    }

    @Override
    public List<SyntaxTreeNode> children() {
        return Collections.unmodifiableList(children);
    }
}