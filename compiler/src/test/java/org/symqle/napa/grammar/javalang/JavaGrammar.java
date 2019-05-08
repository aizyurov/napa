package org.symqle.napa.grammar.javalang;

import org.symqle.napa.compiler.grammar.NapaCompiler;
import org.symqle.napa.parser.Parser;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by aizyurov on 10/27/17.
 */
public class JavaGrammar {

    private static Parser parser = createParser();

    public static Parser createParser() {
        try {
            return new NapaCompiler().compile(new InputStreamReader(JavaGrammar.class.getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid java grammar");
        }
    }

    public static Parser getParser() {
        return parser;
    }
}
