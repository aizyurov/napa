package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Set;

/**
 * @author lvovich
 */
public class LiteralTest extends TestCase {
    
    private final CompiledGrammar g;

    public LiteralTest() throws IOException {
        g = JavaGrammar.getGrammar();
    }

    public void testInteger() throws Exception {
        runTest("0");
        runTest("2");
        runTest("0372");
        runTest("0xDada_Cafe");
        runTest("1996");
        runTest("0x00_FF__00_FF");
        runTest("0b1111_0000");
        runTest("0B1111_1111");
        runTest("0xC0B0L");
        runTest("0l");
        runTest("0777L");
    }

    public void testFloat() throws Exception {
        runTest("1e1f");
        runTest("2.f");
        runTest(".3f");
        runTest("3.14f");
        runTest("6.022137e+23f");
        runTest("6.022137e-23f");
        runTest("1e1");
        runTest("1E1");
        runTest("2.");
        runTest(".3");
        runTest("0.0");
        runTest("3.14");
        runTest("1e-9d");
        runTest("1E9d");
        runTest("1e137");
    }

    public void testBoolean() throws Exception {
        runTest("true");
        runTest("false");
    }

    public void testCharacter() throws Exception {
        runTest("'a'");
        runTest("'%'");
        runTest("'\\t'");
        runTest("'\\\\'");
        runTest("'\\''");
        runTest("'\\177'");
        runTest("'™'");
    }

    public void testString() throws Exception {
        runTest("\"\"");
        runTest("\"\\\"\"");
        runTest("\"This is a string\"");
        runTest("\"This is a \\nstring\"");
    }

    private void runTest(final String source) throws IOException {
        Set<SyntaxTreeNode> forest = new Parser(g).parse("Literal", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.value());
    }
}
