package org.symqle.epic.grammar;

/**
 * Created by aizyurov on 8/19/16.
 */
public class Symbol {

    public enum Type {
        TOKEN,
        NONTERMINAL
    }
    private final String name;
    private final Type type;

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
