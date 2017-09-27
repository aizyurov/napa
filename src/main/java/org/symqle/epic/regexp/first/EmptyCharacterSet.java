package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class EmptyCharacterSet extends AbstractCharacterSet {

    public EmptyCharacterSet() {
        CharacterSetRegistry.register(this);
    }

    @Override
    public boolean contains(char c) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof CharacterSet)) return false;

        CharacterSet that = (CharacterSet) o;

        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            if (that.contains(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
