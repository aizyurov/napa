package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @author lvovich
 */
public class UnannTypeTest extends TestCase {

    private final Parser g;

    public UnannTypeTest() throws IOException {
        g = JavaGrammar.getParser();
    }

    public void testPrimitive() throws Exception {
        runTest("int", "UnannType");
        runTest("long", "UnannType");
        runTest("short", "UnannType");
        runTest("char", "UnannType");
        runTest("double", "UnannType");
        runTest("float", "UnannType");
        runTest("boolean", "UnannType");
    }

    public void testClassOrInterfaceType() throws Exception {
        runTest("String", "UnannType");
        runTest("java.lang.String", "UnannType");

    }

    public void testParameterizedType() throws Exception {
        runTest("List<String>", "UnannType");
        runTest("Map<String, Set<Integer>>", "UnannType");

    }

    public void testArrayType() throws Exception {
        runTest("int[]", "UnannType");
        runTest("long[]", "UnannType");
        runTest("short[]", "UnannType");
        runTest("char[]", "UnannType");
        runTest("double[]", "UnannType");
        runTest("float[]", "UnannType");
        runTest("boolean[]", "UnannType");
        runTest("boolean[][]", "UnannType");
        runTest("String[]", "UnannType");
        runTest("java.lang.String[]", "UnannType");
        runTest("List<String>[]", "UnannType");
        runTest("List<int[]>", "UnannType");

    }


    private void runTest(final String source, final String expected) throws IOException {
        List<SyntaxTree> forest = g.parse(expected, new StringReader(source), 1000);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
    }
}
