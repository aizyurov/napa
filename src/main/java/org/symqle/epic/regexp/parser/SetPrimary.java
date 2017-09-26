package org.symqle.epic.regexp.parser;

/**
 * @author lvovich
 */
public class SetPrimary implements Primary {
    private final Set set;

    public SetPrimary(final Set set) {
        this.set = set;
    }
}
