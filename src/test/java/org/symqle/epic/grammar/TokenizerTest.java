package org.symqle.epic.grammar;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;

/**
 * @author lvovich
 */
public class TokenizerTest extends TestCase {

    public void testAll() throws Exception {
        Reader reader = new StringReader("MYTOKEN nonTerminal @MYMACRO $WHITESPACE \"regexp\" \n'literal'");
        Tokenizer tokenizer = new Tokenizer(reader);
        Assert.assertFalse(tokenizer.take("NT").getName().equals("NT"));

        Token token = tokenizer.take("TOKEN");
        Assert.assertEquals("TOKEN", token.getName());
        Assert.assertEquals("MYTOKEN", token.getValue());
        System.out.println(token);

        token = tokenizer.take("NT");
        Assert.assertEquals("NT", token.getName());
        Assert.assertEquals("nonTerminal", token.getValue());
        System.out.println(token);

        token = tokenizer.take("MACRO");
        Assert.assertEquals("MACRO", token.getName());
        Assert.assertEquals("@MYMACRO", token.getValue());

        token = tokenizer.take("IGNORED");
        Assert.assertEquals("IGNORED", token.getName());
        Assert.assertEquals("$WHITESPACE", token.getValue());

        token = tokenizer.take("REGEXP");
        Assert.assertEquals("REGEXP", token.getName());
        Assert.assertEquals("\"regexp\"", token.getValue());

        token = tokenizer.take("LITERAL");
        Assert.assertEquals("LITERAL", token.getName());
        Assert.assertEquals("'literal'", token.getValue());
        System.out.println(token);

        token = tokenizer.take("EOF");
        Assert.assertEquals("EOF", token.getName());
        Assert.assertEquals("", token.getValue());
        System.out.println(token);
    }

    public void testNoSpace() throws Exception {
        Reader reader = new StringReader("@MYMACRO\"regexp\"MYTOKEN'literal'@MYMACRO");
        Tokenizer tokenizer = new Tokenizer(reader);

        Token token;
        token = tokenizer.take("MACRO");
        Assert.assertEquals("MACRO", token.getName());
        Assert.assertEquals("@MYMACRO", token.getValue());

        token = tokenizer.take("REGEXP");
        Assert.assertEquals("REGEXP", token.getName());
        Assert.assertEquals("\"regexp\"", token.getValue());

        token = tokenizer.take("TOKEN");
        Assert.assertEquals("TOKEN", token.getName());
        Assert.assertEquals("MYTOKEN", token.getValue());

        token = tokenizer.take("LITERAL");
        Assert.assertEquals("LITERAL", token.getName());
        Assert.assertEquals("'literal'", token.getValue());

        token = tokenizer.take("MACRO");
        Assert.assertEquals("MACRO", token.getName());
        Assert.assertEquals("@MYMACRO", token.getValue());
    }

    public void testUnclosedRegexp() throws Exception {
        Reader reader = new StringReader("\"regexp");
        try {
            Tokenizer tokenizer = new Tokenizer(reader);
            Token token;
            token = tokenizer.take("REGEXP");
            Assert.fail("Expected IllegalArgumentException but returned " + token);
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    public void testUnclosedLiteral() throws Exception {
        Reader reader = new StringReader("'literal\n'");
        try {
            Tokenizer tokenizer = new Tokenizer(reader);
            Token token;
            token = tokenizer.take("REGEXP");
            Assert.fail("Expected IllegalArgumentException but returned " + token);
        } catch (IllegalArgumentException e) {
            // ok
        }

    }


}
