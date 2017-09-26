package org.symqle.epic.regexp.parser;

/**
 * @author lvovich
 */
public class CharacterPrimary implements Primary {

    private final char theCharacter;

    public CharacterPrimary(final char theCharacter) {
        this.theCharacter = theCharacter;
    }
}
