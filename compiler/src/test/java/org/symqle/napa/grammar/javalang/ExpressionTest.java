package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.CompiledGrammar;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.StringReader;
import java.util.List;

public class ExpressionTest extends TestCase {

    private final CompiledGrammar g;

    public ExpressionTest() {
        g = JavaGrammar.getGrammar();
    }

    public void testCastExpression() throws Exception {
        Parser parser = new Parser();
        final List<SyntaxTree> forest = parser.parse(g, "CastExpression", new StringReader("(List<?>) o"));
        Assert.assertEquals(1, forest.size());
        final SyntaxTree tree = forest.get(0);
        tree.print(System.out);
        System.out.println(parser.stats());
    }

    public void testVariableName() throws Exception {
        Parser parser = new Parser();
        final List<SyntaxTree> forest = parser.parse(g, "Expression", new StringReader("a"));
        Assert.assertEquals(1, forest.size());
        final SyntaxTree tree = forest.get(0);
        tree.print(System.out);
        System.out.println(parser.stats());
    }

}
