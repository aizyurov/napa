package org.symqle.napa.parser;

import java.util.Collections;

/**
 * Created by aizyurov on 10/31/17.
 */
public class EmptyNode implements RawSyntaxNode {

    final int tag;
    final String name;
    final int line;
    final int pos;

    public EmptyNode(final int tag, final String name, final int line, final int pos) {
        this.tag = tag;
        this.name = name;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public SyntaxTree toSyntaxTreeNode(SyntaxTree parent) {
        return new BranchNode(name, parent, line, pos, Collections.emptyList());
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPos() {
        return pos;
    }

    @Override
    public int treeSize() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyNode emptyNode = (EmptyNode) o;

        if (line != emptyNode.line) return false;
        if (pos != emptyNode.pos) return false;
        if (tag != emptyNode.tag) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tag;
        result = 31 * result + line;
        result = 31 * result + pos;
        return result;
    }
}
