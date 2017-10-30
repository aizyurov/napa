package org.symqle;

import org.symqle.epic.gparser.SyntaxTree;

import java.util.Collections;
import java.util.List;

/**
 * Created by aizyurov on 10/31/17.
 */
public class LeafNode extends AbstractNode {

    private final String name;
    private final String value;
    private final List<String> preface;

    public LeafNode(String name, String value, SyntaxTree parent, List<String> preface, int line, int pos) {
        super(line, pos, parent);
        this.name = name;
        this.value = value;
        this.preface = preface;
    }

    @Override
    public List<String> getPreface() {
        return Collections.unmodifiableList(preface);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public List<SyntaxTree> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String getSource() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String prefaceElem: preface) {
            stringBuilder.append(prefaceElem);
        }
        stringBuilder.append(value);
        return stringBuilder.toString();
    }
}
