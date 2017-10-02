package org.symqle.epic.regexp.first;

import java.util.BitSet;
import java.util.List;

/**
 * Created by aizyurov on 9/27/17.
 */
public class AbstractCharacterSet implements CharacterSet {
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

    @Override
    public boolean contains(char c) {
        return bitSet.get(c);
    }

    protected void set(int from, int to, boolean value) {
        bitSet.set(from, to, value);
    }

    protected void xor(AbstractCharacterSet other) {
        bitSet.xor(other.bitSet);
    }

    protected void or(AbstractCharacterSet other) {
        bitSet.or(other.bitSet);
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

    public static AbstractCharacterSet unionAll(List<AbstractCharacterSet> characterSets) {
        AbstractCharacterSet set = new AbstractCharacterSet();
        for (AbstractCharacterSet characterSet: characterSets) {
            set.bitSet.or(characterSet.bitSet);
        }
        return set;
    }

}
