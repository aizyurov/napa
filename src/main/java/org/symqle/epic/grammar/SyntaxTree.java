package org.symqle.epic.grammar;

import java.util.List;

/**
 * @author lvovich
 */
public interface SyntaxTree {

    String name();
    List<SyntaxTree> children();
    String value();
    int line();
    int pos();
}
