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
        String source = "@Transient int";
        List<SyntaxTree> forest = g.parse("PrimitiveType", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals("PrimitiveType", tree.getName());
        Assert.assertEquals(2, tree.getChildren().size());
        Assert.assertEquals("@", tree.getChildren().get(0).getValue());
        Assert.assertEquals("@", tree.getChildren().get(0).getChildren().get(0).getChildren().get(0).getValue());
        Assert.assertEquals("Transient", tree.getChildren().get(0).getChildren().get(0).getChildren().get(1).getValue());
        Assert.assertEquals("int", tree.getChildren().get(1).getValue());
        Assert.assertEquals("NumericType", tree.getChildren().get(1).getName());
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
        runTest("List<int[]>", "Type");

    }


    private void runTest(final String source, final String expected) throws IOException {
        List<SyntaxTree> forest = g.parse(expected, new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
    }
}
