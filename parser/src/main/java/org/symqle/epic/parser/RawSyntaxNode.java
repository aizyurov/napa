package org.symqle.epic.parser;

/**
 * @author lvovich
 */
public interface RawSyntaxNode {

    SyntaxTree toSyntaxTreeNode(SyntaxTree parent);
    int getLine();
    int getPos();

}
