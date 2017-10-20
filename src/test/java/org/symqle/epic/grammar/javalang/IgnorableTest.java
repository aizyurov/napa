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
public class IgnorableTest extends TestCase {

    private final CompiledGrammar g;

    public IgnorableTest() throws IOException {
        g = getGrammar();
    }

    public void testWhitespace() throws Exception {
        runTest("  ", "1996");
        runTest("\r\n\t\f", "1996");
    }


    private CompiledGrammar getGrammar() throws IOException {
        return new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
    }

    private void runTest(final String preface, String source) throws IOException {

        Set<SyntaxTreeNode> forest = new Parser(g).parse("Literal", new StringReader(preface + source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.value());
        Assert.assertEquals(1, tree.preface().size());
        Assert.assertEquals(preface, tree.preface().get(0));
    }
}
