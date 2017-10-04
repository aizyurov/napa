package org.symqle.epic.gparser;

import java.util.List;

/**
 * @author lvovich
 */
public interface SyntaxTreeNode {

    String name();
    String value();
    List<String> preface();
    String text();
    String file();
    int line();
    int pos();
    boolean isTermimal();
    List<SyntaxTreeNode> children();
}
