package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.GrammarException;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class IdentifierTest extends TestCase {

    private final Parser g;

    public IdentifierTest() throws IOException {
        g = JavaGrammar.getParser();
    }

    public void testIdentifier() throws Exception {
        runTest("String");
        runTest("i3");
        runTest("MAX_VALUE");
        runTest("isLetterOrDigit");
        runTest("$1996");
        try {
            runTest("1a");
            fail("1a is not an identifier");
        } catch (GrammarException e) {
            Assert.assertTrue(e.getMessage().contains("Unrecognized input"));
        }
    }


    private void runTest(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("Identifier", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getValue());
    }
}
