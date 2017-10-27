package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

/**
 * @author lvovich
 */
public class TypeTest extends TestCase {

    private final Parser g;

    public TypeTest() throws IOException {
        g = JavaGrammar.getParser();
    }

    public void testPrimitive() throws Exception {
        runTest("int", "Type");
        runTest("long", "Type");
        runTest("short", "Type");
        runTest("char", "Type");
        runTest("double", "Type");
        runTest("float", "Type");
        runTest("boolean", "Type");
    }

    public void testAnnotatedPrimitive() throws Exception {
        String source = "#Annotation# int";
        Set<SyntaxTreeNode> forest = g.parse("PrimitiveType", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals("PrimitiveType", tree.name());
        Assert.assertEquals(2, tree.children().size());
        Assert.assertEquals("#Annotation#", tree.children().get(0).value());
        Assert.assertEquals("int", tree.children().get(1).value());
        Assert.assertEquals("NumericType", tree.children().get(1).name());
    }

    public void testClassOrInterfaceType() throws Exception {
        runTest("String", "ClassOrInterfaceType");
        runTest("java.lang.String", "ClassOrInterfaceType");

        runTest("String", "Type");
        runTest("java.lang.String", "Type");
    }

    public void testParameterizedType() throws Exception {
        runTest("List<String>", "ClassOrInterfaceType");
        runTest("Map<String, Set<Integer>>", "ClassOrInterfaceType");

        runTest("List<String>", "Type");
        runTest("List<? extends Collection<String>>", "Type");
        runTest("List<? super Collection<String>>", "Type");
    }

    public void testTypeArguments() throws Exception {
        runTest("<String>", "TypeArguments");
    }

    public void testArrayType() throws Exception {
        runTest("int[]", "Type");
        runTest("long[]", "Type");
        runTest("short[]", "Type");
        runTest("char[]", "Type");
        runTest("double[]", "Type");
        runTest("float[]", "Type");
        runTest("boolean[]", "Type");
        runTest("boolean[][]", "Type");
        runTest("String[]", "Type");
        runTest("java.lang.String[]", "Type");
        runTest("List<String>[]", "Type");

    }


    private void runTest(final String source, final String expected) throws IOException {
        Set<SyntaxTreeNode> forest = g.parse(expected, new StringReader(source), 1000);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.text());
    }
}
