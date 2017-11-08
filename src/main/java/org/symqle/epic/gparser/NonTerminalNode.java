package org.symqle.epic.gparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class NonTerminalNode implements RawSyntaxNode {

    private final int tag;
    private final List<RawSyntaxNode> children;



    public NonTerminalNode(int tag, List<RawSyntaxNode> children) {
        this.tag = tag;
        this.children = children;
    }

    @Override
    public SyntaxTree toSyntaxTreeNode(SyntaxTree parent, CompiledGrammar grammar) {
        return new BranchNode(grammar.getNonTerminalName(tag), parent, getLine(), getPos(), children, grammar);
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
        int result = tag;
        result = 31 * result + children.hashCode();
        return result;
    }
}
