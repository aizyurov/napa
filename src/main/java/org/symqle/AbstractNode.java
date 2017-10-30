package org.symqle;

import org.symqle.epic.gparser.SyntaxTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aizyurov on 10/31/17.
 */
public abstract class AbstractNode implements SyntaxTree {

    private final SyntaxTree parent;
    private final int line;
    private final int pos;

    protected AbstractNode(int line, int pos, SyntaxTree parent) {
        this.line = line;
        this.pos = pos;
        this.parent = parent;
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
    public SyntaxTree getParent() {
        return parent;
    }

}
