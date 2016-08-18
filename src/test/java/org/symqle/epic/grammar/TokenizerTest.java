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
        Reader reader = new StringReader("MYTOKEN nonTerminal @MYSTATE $WHITESPACE \"regexp\" \n'literal'");
        Tokenizer tokenizer = new Tokenizer(reader);
        Assert.assertFalse(tokenizer.preview().getName().equals(TokenType.NT));

        Token token = tokenizer.take();
        Assert.assertEquals(TokenType.TOKEN, token.getName());
        Assert.assertEquals("MYTOKEN", token.getValue());
        System.out.println(token);

        token = tokenizer.take();
        Assert.assertEquals(TokenType.NT, token.getName());
        Assert.assertEquals("nonTerminal", token.getValue());
        System.out.println(token);

        token = tokenizer.take();
        Assert.assertEquals(TokenType.STATE, token.getName());
        Assert.assertEquals("@MYSTATE", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.IGNORED, token.getName());
        Assert.assertEquals("$WHITESPACE", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.REGEXP, token.getName());
        Assert.assertEquals("\"regexp\"", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.LITERAL, token.getName());
        Assert.assertEquals("'literal'", token.getValue());
        System.out.println(token);

        token = tokenizer.take();
        Assert.assertEquals(TokenType.EOF, token.getName());
        Assert.assertEquals("", token.getValue());
        System.out.println(token);
    }

    public void testNoSpace() throws Exception {
        Reader reader = new StringReader("@MYSTATE\"regexp\"MYTOKEN'literal'@MYSTATE");
        Tokenizer tokenizer = new Tokenizer(reader);

        Token token;
        token = tokenizer.take();
        Assert.assertEquals(TokenType.STATE, token.getName());
        Assert.assertEquals("@MYSTATE", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.REGEXP, token.getName());
        Assert.assertEquals("\"regexp\"", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.TOKEN, token.getName());
        Assert.assertEquals("MYTOKEN", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.LITERAL, token.getName());
        Assert.assertEquals("'literal'", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.STATE, token.getName());
        Assert.assertEquals("@MYSTATE", token.getValue());
    }

    public void testUnclosedRegexp() throws Exception {
        Reader reader = new StringReader("\"regexp");
        try {
            Tokenizer tokenizer = new Tokenizer(reader);
            Token token;
            token = tokenizer.take();
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
            token = tokenizer.take();
            Assert.fail("Expected IllegalArgumentException but returned " + token);
        } catch (IllegalArgumentException e) {
            // ok
        }
    }
    
    public void testTokenDefinition() throws Exception {
        Reader reader = new StringReader("IDENTIFIER=\"[A-Za-z][A-Za-z0-9]*\";");
        Tokenizer tokenizer = new Tokenizer(reader);
        Token token;

        token = tokenizer.take();
        Assert.assertEquals(TokenType.TOKEN, token.getName());
        Assert.assertEquals("IDENTIFIER", token.getValue());
        
        token = tokenizer.take();
        Assert.assertEquals(TokenType.EQ, token.getName());
        Assert.assertEquals("=", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.REGEXP, token.getName());
        Assert.assertEquals("\"[A-Za-z][A-Za-z0-9]*\"", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.SEMICOLON, token.getName());
        Assert.assertEquals(";", token.getValue());

        token = tokenizer.take();
        Assert.assertEquals(TokenType.EOF, token.getName());

    }


}
