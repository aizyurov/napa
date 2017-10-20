package org.symqle.epic.grammar;

import junit.framework.TestCase;
import org.symqle.epic.analyser.lexis.GaLexer;
import org.symqle.epic.analyser.lexis.GaTokenType;
import org.symqle.epic.tokenizer.DfaTokenizer;
import org.symqle.epic.tokenizer.PackedDfa;
import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by aizyurov on 10/4/17.
 */
public class GaLexerTest extends TestCase {

    public void testCompile() throws Exception {
        final PackedDfa<GaTokenType> dfa = new GaLexer().compile();
        String sample = "! \"[ \\n\\r]+\";\n" +
                "unit = { class_definition | interface_definition };";
        final Tokenizer<GaTokenType> tokenizer = new DfaTokenizer<GaTokenType>(dfa, new StringReader(sample));
        for (Token<GaTokenType> token = tokenizer.nextToken(); token != null; token = tokenizer.nextToken()) {
            System.out.println(token);
        }
    }

    public void testReplacement() throws Exception  {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("quoted.txt");
        Reader reader = new InputStreamReader(stream);
        StringBuilder builder = new StringBuilder();
        for (int c = reader.read(); c >=0; c = reader.read()) {
            builder.append((char)c);
        }
        String pattern = builder.toString();
        String replaced = pattern.replaceAll("\\\"", "\"").replaceAll("\\\'", "\'");
        System.out.println(replaced);
    }
}
