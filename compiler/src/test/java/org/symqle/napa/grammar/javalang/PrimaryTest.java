package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.CollectingSyntaxErrorListener;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.StringReader;
import java.util.List;

/**
 * @author lvovich
 */
public class PrimaryTest extends TestCase {

    private final Parser g;

    public PrimaryTest() {
        g = JavaGrammar.getParser();
    }

    public void testThis() throws Exception {

        List<SyntaxTree> forest = g.parse("Primary", new StringReader("this.a"));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree1 = forest.iterator().next();
        Assert.assertEquals("this.a", tree1.getSource());
        tree1.print(System.out);

    }

    public void testThisWithTrailer() throws Exception {

        List<SyntaxTree> forest = g.parse("Primary", new StringReader("this.a   "));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree1 = forest.iterator().next();
        Assert.assertEquals("this.a", tree1.getSource());
        tree1.print(System.out);

    }

    public void testThisWithGarbageTrailer() throws Exception {

        CollectingSyntaxErrorListener errorListener = new CollectingSyntaxErrorListener();
        List<SyntaxTree> forest = g.parse("Primary", new StringReader("this.a;"), null, errorListener);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree1 = forest.iterator().next();
        Assert.assertEquals("this.a", tree1.getSource());
        Assert.assertEquals(1, errorListener.getErrors().size());
        tree1.print(System.out);

    }

    public void testThisAsExpression() throws Exception {

        List<SyntaxTree> forest = g.parse("Expression", new StringReader("a.b[i++] = z.c(d(x), y+z+w(3))"));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
//        tree.print(System.out);

    }

    public void testLambda() throws Exception {
        List<SyntaxTree> forest = g.parse("Expression", new StringReader("() -> {}"));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        tree.print(System.out);

    }

}
