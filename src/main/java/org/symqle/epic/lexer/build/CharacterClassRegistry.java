package org.symqle.epic.lexer.build;

import org.symqle.epic.lexer.build.CharacterSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
class CharacterClassRegistry {

    private final Set<CharacterSet> allCharacterSets;

    private final int[]  characterClasses = new int[1 + Character.MAX_VALUE];

    private final Map<Set<CharacterSet>, Integer> index = new HashMap<>();

    private final Map<CharacterSet, Set<Integer>> reverseIndex = new HashMap<>();

    public CharacterClassRegistry(final Set<CharacterSet> allCharacterSets) {
        this.allCharacterSets = allCharacterSets;
        long start = System.currentTimeMillis();
        init();
        System.out.println("Registry init " + (System.currentTimeMillis() - start));
        revertIndex();
        System.out.println("Registry created in " + (System.currentTimeMillis() - start));
    }

    private void revertIndex() {
        for (CharacterSet characterSet: allCharacterSets) {
            Set<Integer> characterClasses = new HashSet<>();
            for (Set<CharacterSet> key: index.keySet()) {
                if (key.contains(characterSet)) {
                    Integer setIndex = index.get(key);
                    characterClasses.add(setIndex);
                }
            }
            reverseIndex.put(characterSet, characterClasses);
        }
    }

    public Set<Integer> getCharacterClasses(CharacterSet characterSet) {
        return reverseIndex.get(characterSet);
    }

    private void init() {
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            addChar(c);
        }
        addChar(Character.MAX_VALUE);
    }

    private void addChar(char c) {
        Set<CharacterSet> characterSets = new HashSet<>();
        for (CharacterSet candidate: allCharacterSets) {
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

    private int indexOf(Set<CharacterSet> characterClass) {
        Integer i = this.index.getOrDefault(characterClass, this.index.size());
        index.put(characterClass, i);
        return i;
    }



}
