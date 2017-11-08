package org.symqle.epic.gparser;

import java.util.Collections;

/**
 * Created by aizyurov on 10/31/17.
 */
public class EmptyNode implements RawSyntaxNode {

    final int tag;
    final int line;
    final int pos;

    public EmptyNode(int tag, int line, int pos) {
        this.tag = tag;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public SyntaxTree toSyntaxTreeNode(SyntaxTree parent, CompiledGrammar grammar) {
        return new BranchNode(grammar.getNonTerminalName(tag), parent, line, pos, Collections.emptyList(), grammar);
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
