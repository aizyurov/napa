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


    public void testDecimal() throws Exception {
        runTest(getGrammar(), "0");
        runTest(getGrammar(), "2");
        runTest(getGrammar(), "0372");
        runTest(getGrammar(), "0xDada_Cafe");
        runTest(getGrammar(), "1996");
        runTest(getGrammar(), "0x00_FF__00_FF");
    }

    private CompiledGrammar getGrammar() throws IOException {
        return new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
    }

    private void runTest(final CompiledGrammar g, final String source) throws IOException {
        Set<SyntaxTreeNode> forest = new Parser(g).parse("Literal", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.value());
    }
}
