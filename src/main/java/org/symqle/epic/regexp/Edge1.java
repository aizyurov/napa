package org.symqle.epic.regexp;

import org.symqle.epic.regexp.first.CharacterSet;
import org.symqle.epic.regexp.first.FirstFaNode;

/**
* Created by aizyurov on 9/27/17.
*/
public class Edge1 {
    private final CharacterSet characterSet;
    private final FirstFaNode to;

    public Edge1(CharacterSet characterSet, FirstFaNode to) {
        this.characterSet = characterSet;
        this.to = to;
    }

    public CharacterSet getCharacterSet() {
        return characterSet;
    }

    public FirstFaNode getTo() {
        return to;
    }
}
