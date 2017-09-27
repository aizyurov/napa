package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class AnyCharacterSet extends AbstractCharacterSet {

    public AnyCharacterSet() {
        set(Character.MIN_VALUE, Character.MAX_VALUE + 1, true);
        CharacterSetRegistry.register(this);
    }

}
