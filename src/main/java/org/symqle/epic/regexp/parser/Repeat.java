package org.symqle.epic.regexp.parser;

/**
 * @author lvovich
 */
public class Repeat {

    private final Primary primary;
    private final Repetitions repetitions;

    public Repeat(final Primary primary, final Repetitions repetitions) {
        this.primary = primary;
        this.repetitions = repetitions;
    }

    public static enum Repetitions {
        ZERO_OR_MORE,
        ZERO_OR_ONE,
        ONE_OR_MORE
    }

}
