package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
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
        List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sample.txt")), 1000);
        Assert.assertEquals(1, forest.size());
    }

    public void testEDS() throws Exception {
        System.out.println("=== Benchmark ===");
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
        }
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
        }
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
        }
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
            SyntaxTree tree = forest.get(0);
            System.out.println("Tree size: " + tree.treeSize());
            tree.print(new FileOutputStream("true2"));

        }
    }

    public void testManyStringsConcat() throws Exception {
        System.out.println("=== Benchmark ===");
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ManyStringsConcat.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
        }
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ManyStringsConcat.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
        }
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ManyStringsConcat.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
        }
        {
            List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("ManyStringsConcat.txt"))), 1000);
            Assert.assertEquals(1, forest.size());
            SyntaxTree tree = forest.get(0);
            tree.print(new FileOutputStream("true2"));

        }
    }


    private BufferedReader reader() {
        return new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("EnvironmentDeploymentService.txt")));
    }

}
