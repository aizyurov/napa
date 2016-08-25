package org.symqle.epic.grammar;

import java.util.Collections;
import java.util.List;

/**
 * @author lvovich
 */
public class SyntaxLeaf implements SyntaxTree {

    private final Token token;

    public SyntaxLeaf(final Token token) {
        this.token = token;
    }

    @Override
    public String name() {
        return token.getName().toString();
    }

    @Override
    public List<SyntaxTree> children() {
        return Collections.emptyList();
    }

    @Override
    public String value() {
        return token.getValue();
    }

    @Override
    public String toString() {
        return name() + "(" + value() + ")";
    }

    @Override
    public int line() {
        return token.getLine();
    }

    @Override
    public int pos() {
        return token.getPos();
    }
}
