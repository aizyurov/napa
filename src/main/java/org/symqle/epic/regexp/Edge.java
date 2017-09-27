package org.symqle.epic.regexp;

import org.symqle.epic.regexp.first.CharacterSet;
import org.symqle.epic.regexp.first.FirstFaState;

/**
* Created by aizyurov on 9/27/17.
*/
public class Edge {
    private final CharacterSet characterSet;
    private final FirstFaState to;

    public Edge(CharacterSet characterSet, FirstFaState to) {
        this.characterSet = characterSet;
        this.to = to;
    }

    public CharacterSet getCharacterSet() {
        return characterSet;
    }

    public FirstFaState getTo() {
        return to;
    }
}
