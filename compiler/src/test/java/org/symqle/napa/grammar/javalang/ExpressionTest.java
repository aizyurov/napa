package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.StringReader;
import java.util.List;

public class ExpressionTest extends TestCase {

    private final Parser g;

    public ExpressionTest() {
        g = JavaGrammar.getParser();
    }

    public void testCastExpression() throws Exception {
        final List<SyntaxTree> forest = g.parse("CastExpression", new StringReader("(List<?>) o"));
        Assert.assertEquals(1, forest.size());
        final SyntaxTree tree = forest.get(0);
        tree.print(System.out);
        System.out.println(g.stats());
    }

    public void testVariableName() throws Exception {
        final List<SyntaxTree> forest = g.parse("Expression", new StringReader("a"));
        Assert.assertEquals(1, forest.size());
        final SyntaxTree tree = forest.get(0);
        tree.print(System.out);
        System.out.println(g.stats());
    }

}
