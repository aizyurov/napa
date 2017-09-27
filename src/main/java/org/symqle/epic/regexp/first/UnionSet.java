package org.symqle.epic.regexp.first;

/**
 * Created by aizyurov on 9/27/17.
 */
public class UnionSet extends AbstractCharacterSet {

    public UnionSet(AbstractCharacterSet first, AbstractCharacterSet second) {
        or(first);
        or(second);
        CharacterSetRegistry.register(this);
    }


}
