package org.symqle.napa.parser;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 10/31/17.
 */
public class BranchNode extends SyntaxTree {

    private final String name;
    private List<SyntaxTree> children;

    public BranchNode(String name, SyntaxTree parent, int line, int pos, List<RawSyntaxNode> rawSyntaxNodes) {
        super(parent, line, pos);
        this.name = name;
        this.children = rawSyntaxNodes.stream().map(rawSyntaxNode -> rawSyntaxNode.toSyntaxTreeNode(this)).collect(Collectors.toList());
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < children.size(); i++) {
            SyntaxTree child = children.get(i);
            if (i > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(child);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void print(final Writer writer) throws IOException {
        writer.write(getName());
        writer.write("[");
        writer.write(String.valueOf(getLine()));
        writer.write(":");
        writer.write(String.valueOf(getPos()));
        writer.write(("]"));

        if (getChildren().isEmpty()) {
            writer.write("(empty)");
        }
        writer.write("\n");
    }

    @Override
    public int treeSize() {
        int size = 1;
        for (SyntaxTree child: children) {
            size += child.treeSize();
        }
        return size;
    }
}
