package org.symqle.napa.grammar;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.AppendableList;

import java.util.Arrays;
import java.util.Collections;

public class AppendableListTest extends TestCase {

    public void testEmpty() {
        Assert.assertEquals(AppendableList.<String>empty().asList(), Collections.<String>emptyList());
    }

    public void testAppend() {
        Assert.assertEquals(AppendableList.<String>empty().append("a").append("b").asList(), Arrays.asList("a", "b"));
    }
}
