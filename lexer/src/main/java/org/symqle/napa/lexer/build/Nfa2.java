package org.symqle.napa.lexer.build;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lvovich
 */
class Nfa2 {

    private final Collection<NfaNode2> nodes;

    public Nfa2(final Collection<NfaNode2> nodes) {
        this.nodes = nodes;
    }

    public Dfa toDfa() {

        Set<CharacterSet> allCharacterSets = nodes.stream().flatMap(x -> x.getEdges().stream()).map(NfaNode2.Edge::getCharacterSet).collect(Collectors.toSet());
        CharacterClassRegistry registry = new CharacterClassRegistry(allCharacterSets);

        Map<ImmutableSet<NfaNode2>, DfaNode> dfaNodeMap = new LinkedHashMap<>();
        Set<ImmutableSet<NfaNode2>> processed = new LinkedHashSet<>();
        Set<ImmutableSet<NfaNode2>> queue = new HashSet<>();


        ImmutableSet<NfaNode2> startSet = new ImmutableSet<>(Collections.singleton(nodes.iterator().next()));
        queue.add(startSet);
        DfaNode startDfa = new DfaNode(collectTags(startSet));
        dfaNodeMap.put(startSet, startDfa);

        while (!queue.isEmpty()) {
            ImmutableSet<NfaNode2> nfaSet = queue.iterator().next();
            queue.remove(nfaSet);
            processed.add(nfaSet);
            Map<Integer, Set<NfaNode2>> nodeSetsByCharacterClass = new HashMap<>();
            for (NfaNode2 nfaNode : nfaSet) {
                for (NfaNode2.Edge edge : nfaNode.getEdges()) {
                    Set<Integer> characterClasses = registry.getCharacterClasses(edge.getCharacterSet());
                    for (Integer characterClass : characterClasses) {
                        Set<NfaNode2> nodeSet = nodeSetsByCharacterClass.getOrDefault(characterClass, new HashSet<>());
                        nodeSet.add(edge.getTo());
                        nodeSetsByCharacterClass.put(characterClass, nodeSet);
                    }
                }
            }
            DfaNode currentDfaNode = dfaNodeMap.get(nfaSet);
            for (Map.Entry<Integer, Set<NfaNode2>> entry: nodeSetsByCharacterClass.entrySet()) {
                final ImmutableSet<NfaNode2> nfaNodes = new ImmutableSet<>(entry.getValue());
                if (!processed.contains(nfaNodes)) {
                    DfaNode dfaNode = dfaNodeMap.getOrDefault(nfaNodes, new DfaNode(collectTags(nfaNodes)));
                    dfaNodeMap.put(nfaNodes, dfaNode);
                    queue.add(nfaNodes);
                    currentDfaNode.addEdge(entry.getKey(), dfaNode);
                } else {
                    currentDfaNode.addEdge(entry.getKey(), dfaNodeMap.get(nfaNodes));
                }
            }
        }
        return new Dfa(new ArrayList<>(dfaNodeMap.values()), registry);
    }

    private Set<Integer> collectTags(final ImmutableSet<NfaNode2> nfaNodeSet) {
        return nfaNodeSet.stream()
                .flatMap(n -> n.getTags().stream())
                .collect(Collectors.toSet());


    }

    private static class ImmutableSet<T> implements Iterable<T> {
        /**
         * Returns a sequential {@code Stream} with this collection as its source.
         *
         * <p>This method should be overridden when the {@link #spliterator()}
         * method cannot return a spliterator that is {@code IMMUTABLE},
         * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
         * for details.)
         *
         * @implSpec
         * The default implementation creates a sequential {@code Stream} from the
         * collection's {@code Spliterator}.
         *
         * @return a sequential {@code Stream} over the elements in this collection
         * @since 1.8
         */
        public Stream<T> stream() {
            return theSet.stream();
        }

        private final Set<T> theSet;

        public ImmutableSet(Set<T> theSet) {
            this.theSet = new HashSet<>(theSet);
            hash = theSet.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ImmutableSet<?> that = (ImmutableSet<?>) o;

            return theSet.equals(that.theSet);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        private int hash;

        /**
         * Returns an iterator over the elements in this set.  The elements are
         * returned in no particular order (unless this set is an instance of some
         * class that provides a guarantee).
         *
         * @return an iterator over the elements in this set
         */
        public Iterator<T> iterator() {
            return theSet.iterator();
        }

        /**
         * Performs the given action for each element of the {@code Iterable}
         * until all elements have been processed or the action throws an
         * exception.  Unless otherwise specified by the implementing class,
         * actions are performed in the order of iteration (if an iteration order
         * is specified).  Exceptions thrown by the action are relayed to the
         * caller.
         *
         * @implSpec
         * <p>The default implementation behaves as if:
         * <pre>{@code
         *     for (T t : this)
         *         action.accept(t);
         * }</pre>
         *
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         * @since 1.8
         */
        public void forEach(Consumer<? super T> action) {
            theSet.forEach(action);
        }
    }

}
