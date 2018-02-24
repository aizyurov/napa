package org.symqle.napa.lexer.build;

import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * Created by aizyurov on 9/27/17.
 */
public class CharacterSet {
    private BitSet bitSet = new BitSet(Character.MAX_VALUE + 1);

    public IntStream stream() {
        return bitSet.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;

        CharacterSet that = (CharacterSet) o;

        return bitSet.equals(that.bitSet);
    }

    private int hash;

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            h = bitSet.hashCode();
            hash = h;
        }
        return h;
    }

    public boolean contains(char c) {
        return bitSet.get(c);
    }

    public static CharacterSet any() {
        CharacterSet set = new CharacterSet();
        set.bitSet.set(Character.MIN_VALUE, Character.MAX_VALUE + 1, true);
        return  set;
    }

    public static CharacterSet complement(CharacterSet complemented) {
        CharacterSet set = any();
        set.bitSet.xor(complemented.bitSet);
        return set;
    }

    public static CharacterSet empty() {
        return new CharacterSet();
    }

    public static CharacterSet range(char from, char to) {
        CharacterSet set = new CharacterSet();
        if (from > to) {
            throw new IllegalArgumentException("Invalid range " + from + "-" + to);
        }
        set.bitSet.set(from, to + 1, true);
        return set;
    }

    public static CharacterSet single(char c) {
        CharacterSet set = new CharacterSet();
        set.bitSet.set(c, true);
        return set;
    }

    public static CharacterSet union(CharacterSet first, CharacterSet second) {
        CharacterSet set = new CharacterSet();
        set.bitSet.or(first.bitSet);
        set.bitSet.or(second.bitSet);
        return set;
    }

}
