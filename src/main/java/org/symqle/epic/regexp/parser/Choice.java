package org.symqle.epic.regexp.parser;

import java.util.List;

/**
 * @author lvovich
 */
public class Choice {

    private final List<Sequence> variants;

    public Choice(final List<Sequence> variants) {
        this.variants = variants;
    }
}
