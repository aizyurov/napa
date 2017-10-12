package org.symqle.epic.grammar;

import junit.framework.TestCase;
import org.symqle.epic.analyser.lexis.GaLexer;
import org.symqle.epic.analyser.lexis.GaTokenType;
import org.symqle.epic.tokenizer.PackedDfa;
import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.DfaTokenizer;
import org.symqle.epic.tokenizer.Tokenizer;

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
}
