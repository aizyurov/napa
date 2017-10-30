package org.symqle.util;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: lvovich
 * Date: 27.10.11
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class TSort {

    private final Map<Integer, Set<Integer>> dependencies;


    public TSort(Map<Integer, Set<Integer>> dependencies) {
        this.dependencies = new HashMap<>(dependencies);
    }


    private Collection<SortedItem> createSortedItems() {
        // parallel arrays
        Map<Integer, SortedItem> sortedItemsMap = new HashMap<Integer, SortedItem>(dependencies.size());
        for (Integer dependent: dependencies.keySet()) {
            sortedItemsMap.put(dependent, new SortedItem(dependent));
        }
        for (Integer dependent: sortedItemsMap.keySet()) {
            final SortedItem dependentItem = sortedItemsMap.get(dependent);
            for (Integer dependency: dependencies.get(dependent)) {
                final SortedItem dependencyItem = sortedItemsMap.get(dependency);
                dependentItem.addLeft(dependencyItem);
                dependencyItem.addRight(dependentItem);
            }
        }
        return sortedItemsMap.values();
    }

    /**
     * Sorts and returns the sorted list
     * this method can be called only once for each instance
     * @return the items topologically sorted
     * @throws IllegalStateException there are cyclic dependencies, cannot sort
     */
    public List<Integer> sort() throws IllegalStateException {
        final List<SortedItem> unsorted;
        final List<SortedItem> sorted;
        // invariants:
        // (0) union of sorted and unsorted is sortedItems
        // (1) if sorted.contains(a) and unsorted.contains(b) than !order(b,a)
        //   i.e. all elements of unsorted are to the right for any element of sorted
        // (2) if sorted.contains(a) and sorted.contains(b) and order(a,b) than sorted.index(a) < sorted.index(b)
        //   i.e. all elements of sorted are properly ordered
        // (3) if element has no dependencies within unsorted, it is to the left from any item having dependencies in this list
        // (4) each sortedItem in unsorted has correcs hasNoDependencies and rightNeighbors within unsorted

        unsorted = new LinkedList<SortedItem>(createSortedItems());
        sorted = new ArrayList<SortedItem>(unsorted.size());
        // now invariants (0)-(2) and (4) are true
        // make (3) true
        for (int i=0; i<unsorted.size(); i++) {
            if (unsorted.get(i).hasNoDependencies()) {
                // this remove-add keeps in place elements with index>i,
                // so we can safe continue loop by i
                final SortedItem moved = unsorted.remove(i);
                unsorted.add(0, moved);
            }
        }
        // all invariants are true
        // move to empty unsorted keeping invariants
        while (unsorted.size()>0) {
            if (unsorted.get(0).hasNoDependencies()) {
                final SortedItem moved = unsorted.remove(0);
                sorted.add(moved);
                // restore invariant (4) and (3)
                for (SortedItem right: moved.getRightNeighbors()) {
                    right.removeLeft(moved);
                    if (right.hasNoDependencies()) {
                        unsorted.remove(right);
                        unsorted.add(0, right);
                    }
                }
            } else {
                List<Integer> cycle = new ArrayList<>();
                for (SortedItem sortedItem: unsorted) {
                    cycle.add(sortedItem.getItem());
                }
                throw new CyclicDependencyException(cycle);
            }
        }
        // done: unsorted.size()=0 && invariant(2) && invariant (0) => sorted is the desired result
        List<Integer> result = new ArrayList<Integer>(sorted.size());
        for (SortedItem sortedItem: sorted) {
            result.add(sortedItem.getItem());
        }
        return result;
    }

    private class SortedItem {
        private Set<SortedItem> leftNeighbors = new HashSet<SortedItem>();
        private Set<SortedItem> rightNeighbors = new HashSet<SortedItem>();
        private final Integer item;

        private SortedItem(Integer item) {
            this.item = item;
        }

        public void addLeft(final SortedItem left) {
            leftNeighbors.add(left);
        }
        public void addRight(final SortedItem right) {
            rightNeighbors.add(right);
        }

        public boolean hasNoDependencies() {
            return leftNeighbors.isEmpty();
        }

        public void removeLeft(SortedItem item) {
            leftNeighbors.remove(item);
        }

        public List<SortedItem> getRightNeighbors() {
            return new ArrayList<SortedItem>(rightNeighbors);
        }

        public Integer getItem() {
            return item;
        }
    }

    public class CyclicDependencyException extends RuntimeException {
        private final List<Integer> cycle;

        public CyclicDependencyException(List<Integer> cycle) {
            this.cycle = cycle;
        }

        public List<Integer> getCycle() {
            return cycle;
        }
    }

}
