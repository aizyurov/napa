package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.CompiledGrammar;
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
    private final CompiledGrammar g;

    public RealClassTest() {
        g = JavaGrammar.getGrammar();
    }

    public void testAll() throws Exception {
        List<SyntaxTree> forest = new Parser().parse(g, "CompilationUnit", new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sample.txt")));
        Assert.assertEquals(1, forest.size());
    }


    public void testEDS() throws Exception {
        Parser parser = new Parser();
        System.out.println("=== Benchmark ===");
        for (int i=0; i< 50; i++)
        {
            final long startTs = System.currentTimeMillis();
            List<SyntaxTree> forest = parser.parse(g, "CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))));
            Assert.assertEquals(1, forest.size());
            System.out.println(System.currentTimeMillis() -startTs);
        }
        System.out.println(parser.stats());
        System.out.println("=== Benchmark end ===");
        {
            List<SyntaxTree> forest = parser.parse(g, "CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))));
            Assert.assertEquals(1, forest.size());
            SyntaxTree tree = forest.get(0);
            System.out.println("Tree size: " + tree.treeSize());
            System.out.println(parser.stats());


            tree.print(new FileOutputStream("lexer2.tree"));

        }
    }

    public void testAbstractList() throws Exception {
        Parser parser = new Parser();
        List<SyntaxTree> forest = parser.parse(g, "CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("AbstractList.j8"))));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(parser.stats());

    }

    public void testHashMap() throws Exception {
        Parser parser = new Parser();
        List<SyntaxTree> forest = parser.parse(g, "CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("HashMap.j8"))));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(parser.stats());

    }

    public void testConcurrentHashMap() throws Exception {
        Parser parser = new Parser();
        List<SyntaxTree> forest = parser.parse(g, "CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ConcurrentHashMap.j8"))));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(parser.stats());

    }

    public void testManyStringsConcat() throws Exception {
        Parser parser = new Parser();
        {
            List<SyntaxTree> forest = parser.parse(g, "CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ManyStringsConcat.txt"))));
            Assert.assertEquals(1, forest.size());
            System.out.println(parser.stats());
        }
    }


}
