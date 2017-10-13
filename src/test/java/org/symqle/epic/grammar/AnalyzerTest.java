package org.symqle.epic.grammar;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.StringReader;
import java.util.Set;

/**
 * @author lvovich
 */
public class AnalyzerTest extends TestCase {

    public void testTrivial() throws Exception {
        String grammar = "identifier = \"[a-zA-Z][a-zA-Z0-9]*\";\n" +
                "class_declaration = \"class\" identifier \";\" ;\n" +
                "! \" +\" ;";
        CompiledGrammar g = new GaGrammar().parse(new StringReader(grammar));
        String source = "class wow ;";
        Set<SyntaxTreeNode> tree = new Parser(g, "class_declaration", new StringReader(source), 100).parse();
        Assert.assertEquals(1, tree.size());
        System.out.println(tree.iterator().next().text());

    }
}
