package org.symqle.epic.grammar.javalang;

import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by aizyurov on 10/27/17.
 */
public class JavaGrammar {

    private static CompiledGrammar grammar = createGrammar();

    private static CompiledGrammar createGrammar() {
        try {
            return new GaGrammar().parse(new InputStreamReader(JavaGrammar.class.getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid java grammar");
        }
    }

    public static CompiledGrammar getGrammar() {
        return grammar;
    }
}
