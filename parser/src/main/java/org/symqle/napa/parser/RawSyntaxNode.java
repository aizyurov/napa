package org.symqle.napa.parser;

/**
 * @author lvovich
 */
public interface RawSyntaxNode {

    SyntaxTree toSyntaxTreeNode(SyntaxTree parent);
    int getLine();
    int getPos();
    int treeSize();

}
