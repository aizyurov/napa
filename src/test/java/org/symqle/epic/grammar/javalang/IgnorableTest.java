package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.GrammarException;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

/**
 * @author lvovich
 */
public class IgnorableTest extends TestCase {

    private final Parser g;

    public IgnorableTest() throws IOException {
        g = JavaGrammar.getParser();
    }

    public void testWhitespace() throws Exception {
        runTest("  ", "1996");
        runTest("\r\n\t\f", "1996");
    }

    public void testClassicComment() throws Exception {
        runTest("/*abc*/", "1996");
        runTest("/**/", "1996");
        runTest("/*a//bc\n*/", "1996");
        // should fail: no embedded comments
        try {
            runTest("/*a/*nested*/bc*/", "1996");
            fail("Nested comments not supported");
        } catch (GrammarException e) {
            Assert.assertTrue(e.getMessage().contains("Unrecognized input bc"));
        }

    }

    public void testLineComment() throws Exception {
        runTest("//abc\n", "1996");
        runTest("//abc\r", "1996");
        runTest("//abc\r\n", "1996");
        runTest("//\n", "1996");
        runTest("//abc/*abc*/\n", "1996");
    }


    private void runTest(final String preface, String source) throws IOException {

        Set<SyntaxTreeNode> forest = g.parse("Literal", new StringReader(preface + source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.value());
        Assert.assertEquals(1, tree.preface().size());
        Assert.assertEquals(preface, tree.preface().get(0));
    }
}
