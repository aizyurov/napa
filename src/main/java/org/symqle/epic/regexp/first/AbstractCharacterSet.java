package org.symqle.epic.regexp.first;

import java.util.BitSet;

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
}
