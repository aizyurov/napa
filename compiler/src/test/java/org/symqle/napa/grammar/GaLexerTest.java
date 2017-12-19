package org.symqle.napa.grammar;

import junit.framework.TestCase;
import org.symqle.napa.compiler.lexis.GaLexer;
import org.symqle.napa.compiler.lexis.GaTokenType;
import org.symqle.napa.tokenizer.DfaTokenizer;
import org.symqle.napa.tokenizer.PackedDfa;
import org.symqle.napa.tokenizer.Token;
import org.symqle.napa.tokenizer.Tokenizer;

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
        final Tokenizer<GaTokenType> tokenizer = new DfaTokenizer<GaTokenType>(dfa, new StringReader(sample), GaTokenType.ERROR);
        for (Token<GaTokenType> token = tokenizer.nextToken(); token.getType() != null; token = tokenizer.nextToken()) {
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
