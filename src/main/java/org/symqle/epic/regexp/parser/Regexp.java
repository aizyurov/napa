package org.symqle.epic.regexp.parser;

/**
 * @author lvovich
 */
public class Regexp {

    private final Choice sequence;

    public Regexp(final Choice sequence) {
        this.sequence = sequence;
    }

}
