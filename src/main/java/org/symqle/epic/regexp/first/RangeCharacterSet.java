package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class RangeCharacterSet extends AbstractCharacterSet {
    private final char from;
    private final char to;

    public RangeCharacterSet(char from, char to) {
        if (from > to) {
            throw new IllegalArgumentException("Invalid range " + from + "-" + to);
        }
        this.from = from;
        this.to = to;
        set(from, to + 1, true);

        CharacterSetRegistry.register(this);

    }

}
