package org.symqle.epic.gparser;

import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class ParserTokenImpl implements ParserToken {

    private final Set<Integer> tags;
    private final List<String> preface;
    private final String text;
    private final int line;
    private final int pos;

    public ParserTokenImpl(final Set<Integer> tags, final List<String> preface, final String text, final int line, final int pos) {
        this.tags = tags;
        this.preface = preface;
        this.text = text;
        this.line = line;
        this.pos = pos;
    }

    @Override
    public boolean matches(final int tag) {
        return tags.contains(tag);
    }

    @Override
    public List<String> preface() {
        return preface;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int pos() {
        return pos;
    }
}
