package org.symqle.epic.grammar;

import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created by aizyurov on 8/19/16.
 */
public class ParserTest extends TestCase {

    public static final String basic = "INENTIFIER = \"[a-zA-Z][a-zA-Z0-9]*\";" +
            "expression = IDENTIFIER | expression '+' IDENTIFIER;";

    public void testBasic() throws Exception {
        Reader reader = new StringReader(basic);
        Parser parser = new Parser();
        final SyntaxTree syntaxTree = parser.parse(reader);
        System.out.println(syntaxTree.toString());

    }
}
