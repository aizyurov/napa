package org.symqle.epic.gparser;

/**
 * @author lvovich
 */
public interface RawSyntaxNode {

    SyntaxTree toSyntaxTreeNode(SyntaxTree parent);
    int getLine();
    int getPos();

}
