package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class EmptyCharacterSet extends AbstractCharacterSet {

    public EmptyCharacterSet() {
        CharacterSetRegistry.register(this);
    }

}
