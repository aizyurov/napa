package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.GrammarException;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

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
            Assert.assertTrue(e.getMessage().contains("Unexpected input \"bc\""));
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

        List<SyntaxTree> forest = g.parse("Literal", new StringReader(preface + source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getValue());
        Assert.assertEquals(1, tree.getPreface().size());
        Assert.assertEquals(preface, tree.getPreface().get(0));
    }
}
