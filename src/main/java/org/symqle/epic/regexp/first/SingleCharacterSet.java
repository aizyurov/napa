package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class SingleCharacterSet extends AbstractCharacterSet {

    public SingleCharacterSet(char theChar) {
        set(theChar, theChar + 1, true);
        CharacterSetRegistry.register(this);

    }

}
