package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTree;

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

        List<SyntaxTree> forest = g.parse("Primary", new StringReader("this.a"), 1000);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree1 = forest.iterator().next();
        Assert.assertEquals("this.a", tree1.getSource());
        tree1.print(System.out);

    }

    public void testThisAsExpression() throws Exception {

        List<SyntaxTree> forest = g.parse("Expression", new StringReader("a.b[i++] = z.c(d(x), y+z+w(3))"), 1000);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
//        tree.print(System.out);

    }

}
