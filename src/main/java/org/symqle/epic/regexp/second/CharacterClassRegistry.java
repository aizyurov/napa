package org.symqle.epic.regexp.second;

import org.symqle.epic.regexp.first.AbstractCharacterSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class CharacterClassRegistry {

    private final Set<AbstractCharacterSet> allCharacterSets;

    private final int[]  characterClasses = new int[1 + Character.MAX_VALUE];

    private final Map<Set<AbstractCharacterSet>, Integer> index = new HashMap<>();

    private final Map<AbstractCharacterSet, Set<Integer>> reverseIndex = new HashMap<>();

    public CharacterClassRegistry(final Set<AbstractCharacterSet> allCharacterSets) {
        this.allCharacterSets = allCharacterSets;
        init();
        revertIndex();
    }

    private void revertIndex() {
        for (AbstractCharacterSet characterSet: allCharacterSets) {
            Set<Integer> characterClasses = new HashSet<>();
            for (Set<AbstractCharacterSet> key: index.keySet()) {
                if (key.contains(characterSet)) {
                    Integer setIndex = index.get(key);
                    characterClasses.add(setIndex);
                }
            }
            reverseIndex.put(characterSet, characterClasses);
        }
    }

    public Set<Integer> getCharacterClasses(AbstractCharacterSet characterSet) {
        return reverseIndex.get(characterSet);
    }

    private void init() {
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            addChar(c);
        }
        addChar(Character.MAX_VALUE);
    }

    private void addChar(char c) {
        Set<AbstractCharacterSet> characterSets = new HashSet<>();
        for (AbstractCharacterSet candidate: allCharacterSets) {
            if (candidate.contains(c)) {
                characterSets.add(candidate);
            }
        }
        characterClasses[c] = indexOf(characterSets);
    }

    public int classOf(char c) {
        return characterClasses[c];
    }

    public int size() {
        return index.size();
    }

    private int indexOf(Set<AbstractCharacterSet> characterClass) {
        Integer i = this.index.getOrDefault(characterClass, this.index.size());
        index.put(characterClass, i);
        return i;
    }



}
