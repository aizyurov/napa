package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class ComplementSet extends AbstractCharacterSet {

    public ComplementSet(AbstractCharacterSet complemented) {
        xor(complemented);
        CharacterSetRegistry.register(this);

    }


}
