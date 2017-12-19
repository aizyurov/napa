package org.symqle.napa.parser;

import java.util.List;

/**
 * @author lvovich
 */
public class NonTerminalNode implements RawSyntaxNode {

    private final int tag;
    private final String name;
    private final List<RawSyntaxNode> children;


    public NonTerminalNode(final int tag, final String name, final List<RawSyntaxNode> children) {
        this.tag = tag;
        this.name = name;
        this.children = children;
    }

    @Override
    public SyntaxTree toSyntaxTreeNode(SyntaxTree parent) {
        return new BranchNode(name, parent, getLine(), getPos(), children);
    }

    @Override
    public int getLine() {
        return children.get(0).getLine();
    }

    @Override
    public int getPos() {
        return children.get(0).getPos();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonTerminalNode that = (NonTerminalNode) o;

        if (tag != that.tag) return false;
        if (!children.equals(that.children)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int result = tag;
            result = 31 * result + children.hashCode();
            hash = result;
            return result;
        } else {
            return h;
        }
    }

    private int hash;

    @Override
    public int treeSize() {
        return 1 + children.stream().mapToInt(RawSyntaxNode::treeSize).sum();
    }
}
