package org.symqle.epic.regexp;

import org.symqle.epic.regexp.first.CharacterSet;
import org.symqle.epic.regexp.second.SecondFaNode;

/**
* Created by aizyurov on 9/27/17.
*/
public class Edge2 {
    private final CharacterSet characterSet;
    private final SecondFaNode to;

    public Edge2(CharacterSet characterSet, SecondFaNode to) {
        this.characterSet = characterSet;
        this.to = to;
    }

    public CharacterSet getCharacterSet() {
        return characterSet;
    }

    public SecondFaNode getTo() {
        return to;
    }
}
