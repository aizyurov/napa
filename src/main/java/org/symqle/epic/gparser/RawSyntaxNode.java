package org.symqle.epic.gparser;

import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;

import java.util.List;

/**
 * @author lvovich
 */
public interface RawSyntaxNode {

    SyntaxTree toSyntaxTreeNode(SyntaxTree parent, CompiledGrammar grammar);
    int getLine();
    int getPos();

}
