package org.symqle.epic.regexp.parser;

import java.util.List;

/**
 * @author lvovich
 */
public class Sequence {

    private final List<Repeat> repetitions;

    public Sequence(final List<Repeat> repetitions) {
        this.repetitions = repetitions;
    }
}
