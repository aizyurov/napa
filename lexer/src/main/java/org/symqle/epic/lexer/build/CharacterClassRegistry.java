package org.symqle.epic.lexer.build;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author lvovich
 */
class CharacterClassRegistry {

    private final Set<CharacterSet> allCharacterSets;

    private final int[]  characterClasses = new int[1 + Character.MAX_VALUE];

    private final Map<ImmutableSet<CharacterSet>, Integer> index = new HashMap<>();

    private final Map<CharacterSet, Set<Integer>> reverseIndex = new HashMap<>();

    public CharacterClassRegistry(final Set<CharacterSet> allCharacterSets) {
        long start = System.currentTimeMillis();
        this.allCharacterSets = allCharacterSets;
        init();
        System.err.println("Registry init " + (System.currentTimeMillis() - start));
        revertIndex();
        System.err.println("Registry created in " + (System.currentTimeMillis() - start));
    }

    private void revertIndex() {
        for (CharacterSet characterSet: allCharacterSets) {
            Set<Integer> characterClasses = new TreeSet<>();
            for (ImmutableSet<CharacterSet> key: index.keySet()) {
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
        characterClasses[c] = indexOf(new ImmutableSet<>(characterSets));
    }

    public int classOf(char c) {
        return characterClasses[c];
    }

    public int size() {
        return index.size();
    }

    private int indexOf(ImmutableSet<CharacterSet> characterClass) {
        Integer i = this.index.getOrDefault(characterClass, this.index.size());
        index.put(characterClass, i);
        return i;
    }

    private static class ImmutableSet<T> {

        private Set<T> theSet;

        public ImmutableSet(final Set<T> theSet) {
            this.theSet = theSet;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ImmutableSet<?> that = (ImmutableSet<?>) o;
            return theSet.equals(that.theSet);
        }

        private int hash;

        @Override
        public int hashCode() {
            int h = hash;
            if (h == 0) {
                h = theSet.hashCode();
                hash = h;
            }
            return h;
        }

        public boolean contains(final Object o) {
            return theSet.contains(o);
        }
    }



}
