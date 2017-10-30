package org.symqle.epic.gparser;

import java.util.List;

/**
 * Created by aizyurov on 10/28/17.
 */
public interface SyntaxTree {

    List<String> getPreface();
    String getName();
    String getValue();
    SyntaxTree getParent();
    List<SyntaxTree> getChildren();
    int getLine();
    int getPos();
    String getSource();

}
