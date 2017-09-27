package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class AnyCharacterSet extends AbstractCharacterSet {

    public AnyCharacterSet() {
        set(Character.MIN_VALUE, Character.MAX_VALUE + 1, true);
        CharacterSetRegistry.register(this);
    }

    private static int computeHash() {
        int hash = 0;
        for (char c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
            hash += c;
        }
        return hash;
    }

}
