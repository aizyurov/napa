package org.symqle.napa.lexer.build;

import java.util.*;

/**
 * @author lvovich
 */
class CharacterClassRegistry {

    private final Set<CharacterSet> allCharacterSets;

    private final int[]  characterClasses = new int[1 + Character.MAX_VALUE];

    private final Map<ImmutableList<CharacterSet>, Integer> index = new HashMap<>();

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
            for (ImmutableList<CharacterSet> key: index.keySet()) {
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
        Map<Character, List<CharacterSet>> characterClassMap = new HashMap<>();
        for (CharacterSet characterSet: allCharacterSets) {
            characterSet.stream().forEach(index -> {
                final Character key = (char) index;
                final List<CharacterSet> characterSets = characterClassMap.getOrDefault(key, new ArrayList<>());
                characterSets.add(characterSet);
                characterClassMap.put(key, characterSets);
            });
        }
        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            characterClasses[c] = indexOf(new ImmutableList<>(characterClassMap.getOrDefault(c, new ArrayList<>())));
        }
        char c = Character.MAX_VALUE;
        characterClasses[c] = indexOf(new ImmutableList<>(characterClassMap.getOrDefault(c, new ArrayList<>())));
    }

    public int classOf(char c) {
        return characterClasses[c];
    }

    public int size() {
        return index.size();
    }

    private int indexOf(ImmutableList<CharacterSet> characterClass) {
        Integer i = this.index.getOrDefault(characterClass, this.index.size());
        index.put(characterClass, i);
        return i;
    }

    private static class ImmutableList<T> {

        private List<T> theList;

        public ImmutableList(final Collection<T> source) {
            this.theList = new ArrayList<>(source);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final ImmutableList<?> that = (ImmutableList<?>) o;
            return theList.equals(that.theList);
        }

        private int hash;

        @Override
        public int hashCode() {
            int h = hash;
            if (h == 0) {
                h = theList.hashCode();
                hash = h;
            }
            return h;
        }

        public boolean contains(final Object o) {
            return theList.contains(o);
        }
    }



}
