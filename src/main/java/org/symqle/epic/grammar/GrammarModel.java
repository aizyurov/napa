package org.symqle.epic.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author lvovich
 */
public class GrammarModel {

    private final Map<String, TokenDefinition> tokens = new HashMap<>();
    private final Map<String, RuleSet> nonTerminals = new HashMap<>();

    public void addGrammar(final SyntaxTree grammar) {
        if (!grammar.name().equals("grammar")) {
            throw new IllegalArgumentException("expected grammar, actually " + grammar.name());
        }
        for (final SyntaxTree statement: grammar.children()) {
            addStatement(statement);
        }
    }

    private void addStatement(final SyntaxTree statement) {
        if (!statement.name().equals("grammar")) {
            throw new IllegalArgumentException("expected statement, actually " + statement.name());
        }
        if (statement.children().size() != 1) {
            throw new IllegalArgumentException("statement has " + statement.children().size() + " children, " + statement);
        }
        SyntaxTree child = statement.children().get(0);
        if (child.name().equals("tokenDefinition")) {
            addTokenDefinition(child);
        } else if (child.name().equals("ruleDefinition")) {
            addRuleDefinition(child);
        } else {
            throw new IllegalArgumentException("Unexpected child of statement: " + child.name());
        }
    }

    private void addTokenDefinition(final SyntaxTree tokenDefinition) {
        if (!tokenDefinition.name().equals("tokenDefinition")) {
            throw new IllegalArgumentException("expected tokenDefinition, actually " + tokenDefinition.name());
        }
        final int precedence;
        Optional<SyntaxTree> mayBePrecedence = tokenDefinition.children().stream().filter(x -> x.name().equals("NUMBER")).findFirst();
        if (mayBePrecedence.isPresent()) {
            precedence = Integer.valueOf(mayBePrecedence.get().value());
        } else {
            precedence = 0;
        }
        Optional<SyntaxTree> mayBeToken = tokenDefinition.children().stream().filter(x -> x.name().equals("TOKEN")).findFirst();
        Optional<SyntaxTree> mayBeIgnored = tokenDefinition.children().stream().filter(x -> x.name().equals("IGNORED")).findFirst();
        Optional<SyntaxTree> mayBeRegexp = tokenDefinition.children().stream().filter(x -> x.name().equals("REGEXP")).findFirst();
        final boolean ignored = mayBeIgnored.isPresent();
        final String name = ignored ? mayBeIgnored.get().value() : mayBeToken.get().value();
        SyntaxTree regexp = mayBeRegexp.get();
        TokenDefinition td = new TokenDefinition(name, ignored, regexp.value(), false, precedence, regexp.line(), regexp.pos());
        if (tokens.put(td.getName(), td) != null) {
            throw new IllegalArgumentException("Dublicate token definitionL " + td.getName());
        }

    }

    private void addRuleDefinition(final SyntaxTree ruleDefinition) {
        throw new RuntimeException("not implemented");
    }



}
