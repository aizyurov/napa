package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.GrammarException;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Set;

/**
 * @author lvovich
 */
public class IdentifierTest extends TestCase {

    private final CompiledGrammar g;

    public IdentifierTest() throws IOException {
        g = getGrammar();
    }

    private CompiledGrammar getGrammar() throws IOException {
        return new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
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
        Set<SyntaxTreeNode> forest = new Parser(g).parse("Identifier", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.value());
    }
}
