package org.symqle.napa.parser;

import java.util.BitSet;
import java.util.Set;

/**
 * @author lvovich
 */
public class TokenProperties {

    private final BitSet bitSet;
    private final boolean ignoreOnly;
    // merge
    private final boolean ignorable;

    public TokenProperties(final boolean ignoreOnly, final boolean ignorable, final Set<Integer> tags) {
        this.ignoreOnly = ignoreOnly;
        this.ignorable = ignorable;
        this.bitSet = new BitSet();
        for (Integer i: tags) {
            bitSet.set(i, true);
        }
    }

    public boolean matches(BitSet expectedTags) {
        return bitSet.intersects(expectedTags);
    }


    public boolean matches(int i) {
        return bitSet.get(i);
    }

    public boolean isIgnoreOnly() {
        return ignoreOnly;
    }

    public boolean isIgnorable() {
        return ignorable;
    }


}
