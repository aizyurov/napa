package org.symqle.epic.gparser;

import org.symqle.AbstractNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 10/31/17.
 */
public class BranchNode extends AbstractNode {

    private final String name;
    private final SyntaxTree parent;
    private List<SyntaxTree> children;

    public BranchNode(String name, SyntaxTree parent, int line, int pos, List<RawSyntaxNode> rawSyntaxNodes, CompiledGrammar grammar) {
        super(line, pos);
        this.name = name;
        this.parent = parent;
        this.children = rawSyntaxNodes.stream().map(rawSyntaxNode -> rawSyntaxNode.toSyntaxTreeNode(this, grammar)).collect(Collectors.toList());
    }

    @Override
    public List<String> getPreface() {
        return children.isEmpty() ? Collections.emptyList() : children.get(0).getPreface();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return children.isEmpty() ? null : children.get(0).getValue();
    }

    @Override
    public SyntaxTree getParent() {
        return parent;
    }

    @Override
    public List<SyntaxTree> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getSource() {
        StringBuilder stringBuilder = new StringBuilder();
        for (SyntaxTree child: children) {
            stringBuilder.append(child.getSource());
        }
        return stringBuilder.toString();
    }

    public void setChildren(List<SyntaxTree> children) {
        if (this.children == null) {
            this.children = new ArrayList<>(children);
        } else {
            throw new IllegalStateException("Cannot set children twice");
        }
    }
}
