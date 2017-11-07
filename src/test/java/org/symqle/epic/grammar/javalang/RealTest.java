package org.symqle.epic.grammar.javalang;

import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;

import java.io.InputStreamReader;

/**
 * Created by aizyurov on 11/8/17.
 */
public class RealTest extends TestCase {

    public void testRegexpSyntaxTreeBuilder() throws Exception {
        Parser g = JavaGrammar.getParser();
        g.parse("CompilationUnit", new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sample.txt")), 100);

    }
}
