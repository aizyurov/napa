package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Set;

/**
 * @author lvovich
 */
public class LiteralTest extends TestCase {

    public void testDecimal() throws Exception {
        CompiledGrammar g = new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
        String source = "1234";
        Set<SyntaxTreeNode> forest = new Parser(g).parse("Literal", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());


    }
}
