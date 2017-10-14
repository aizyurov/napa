package org.symqle.epic.gparser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author lvovich
 */
public class TerminalNode implements SyntaxTreeNode {

    private final String name;
    private final List<String> preface;
    private final String value;
    private final String file;
    private final int line;
    private final int pos;

    public TerminalNode(final String name,
                        final List<String> preface,
                        final String value,
                        final String file,
                        final int line,
                        final int pos) {
        this.name = name;
        this.preface = preface;
        this.value = value;
        this.file = file;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public List<String> preface() {
        return preface;
    }

    @Override
    public String text() {
        return Stream.concat(preface.stream(), Stream.of(value)).reduce("", (a,b) -> a+b);
    }

    @Override
    public String file() {
        return file;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int pos() {
        return pos;
    }

    @Override
    public boolean isTermimal() {
        return true;
    }

    @Override
    public List<SyntaxTreeNode> children() {
        return Collections.emptyList();
    }
}
