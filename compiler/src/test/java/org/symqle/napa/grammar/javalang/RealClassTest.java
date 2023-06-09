package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by aizyurov on 10/28/17.
 */
public class RealClassTest extends TestCase {
    private final Parser g;

    public RealClassTest() {
        g = JavaGrammar.getParser();
    }

    public void testAll() throws Exception {
        List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sample.txt")));
        Assert.assertEquals(1, forest.size());
    }


    public void testEDS() throws Exception {
        System.out.println("=== Benchmark ===");
        for (int i=0; i< 50; i++)
        {
            final long startTs = System.currentTimeMillis();
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))));
            Assert.assertEquals(1, forest.size());
            System.out.println(System.currentTimeMillis() -startTs);
        }
        System.out.println(g.stats());
        System.out.println("=== Benchmark end ===");
        {
            List<SyntaxTree> forest = this.g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))));
            Assert.assertEquals(1, forest.size());
            SyntaxTree tree = forest.get(0);
            System.out.println("Tree size: " + tree.treeSize());
            System.out.println(this.g.stats());


            tree.print(new FileOutputStream("lexer2.tree"));

        }
    }

    public void testAbstractList() throws Exception {
        List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("AbstractList.j8"))));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(g.stats());

    }

    public void testHashMap() throws Exception {
        List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("HashMap.j8"))));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(g.stats());

    }

    public void testConcurrentHashMap() throws Exception {
        List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ConcurrentHashMap.j8"))));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(g.stats());

    }

    public void testManyStringsConcat() throws Exception {
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ManyStringsConcat.txt"))));
            Assert.assertEquals(1, forest.size());
            System.out.println(g.stats());
        }
    }


    private BufferedReader reader() {
        return new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt")));
    }

}
