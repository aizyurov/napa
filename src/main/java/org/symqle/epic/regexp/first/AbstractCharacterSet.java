package org.symqle.epic.regexp.first;

import java.util.BitSet;

/**
 * Created by aizyurov on 9/27/17.
 */
public class AbstractCharacterSet {
    private BitSet bitSet = new BitSet(1 + Character.MAX_VALUE);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ! (o instanceof AbstractCharacterSet)) return false;

        AbstractCharacterSet that = (AbstractCharacterSet) o;

        if (!bitSet.equals(that.bitSet)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return bitSet.hashCode();
    }

    public boolean contains(char c) {
        return bitSet.get(c);
    }

    public static AbstractCharacterSet any() {
        AbstractCharacterSet set = new AbstractCharacterSet();
        set.bitSet.set(Character.MIN_VALUE, Character.MAX_VALUE + 1, true);
        return  set;
    }

    public static AbstractCharacterSet complement(AbstractCharacterSet complemented) {
        AbstractCharacterSet set = new AbstractCharacterSet();
        set.bitSet.xor(complemented.bitSet);
        return set;
    }

    public static AbstractCharacterSet empty() {
        return new AbstractCharacterSet();
    }

    public static AbstractCharacterSet range(char from, char to) {
        AbstractCharacterSet set = new AbstractCharacterSet();
        if (from > to) {
            throw new IllegalArgumentException("Invalid range " + from + "-" + to);
        }
        set.bitSet.set(from, to + 1, true);
        return set;
    }

    public static AbstractCharacterSet single(char c) {
        AbstractCharacterSet set = new AbstractCharacterSet();
        set.bitSet.set(c, true);
        return set;
    }

    public static AbstractCharacterSet union(AbstractCharacterSet first, AbstractCharacterSet second) {
        AbstractCharacterSet set = new AbstractCharacterSet();
        set.bitSet.or(first.bitSet);
        set.bitSet.or(second.bitSet);
        return set;
    }

}
