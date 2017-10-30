package org.symqle;

import org.symqle.epic.gparser.SyntaxTree;

/**
 * Created by aizyurov on 10/31/17.
 */
public abstract class AbstractNode implements SyntaxTree {

    private final int line;
    private final int pos;

    protected AbstractNode(int line, int pos) {
        this.line = line;
        this.pos = pos;
    }

    @Override
    public int getLine() {
        return line;
    }

    @Override
    public int getPos() {
        return pos;
    }
}
