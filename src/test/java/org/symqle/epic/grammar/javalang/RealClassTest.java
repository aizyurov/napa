package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTree;

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
        List<SyntaxTree> forest = g.parse("CompilationUnit", new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sample.txt")), 1000000);
        Assert.assertEquals(1, forest.size());
    }

}
