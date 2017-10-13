package org.symqle.epic.analyser.grammar;

import org.symqle.epic.analyser.lexis.GaLexer;
import org.symqle.epic.analyser.lexis.GaTokenType;
import org.symqle.epic.analyser.lexis.GaTokenizer;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.CompiledRule;
import org.symqle.epic.lexer.TokenDefinition;
import org.symqle.epic.lexer.build.Lexer;
import org.symqle.epic.tokenizer.DfaTokenizer;
import org.symqle.epic.tokenizer.PackedDfa;
import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class GaGrammar {

    private final List<IgnoreStatement> ignored = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();

    private Token<GaTokenType> nextToken;
    private Tokenizer<GaTokenType> tokenizer;

    private void addIgnore(IgnoreStatement ignoreStatement) {
        ignored.add(ignoreStatement);
    }

    private void addRule(Rule rule) {
        rules.add(rule);
    }

    public CompiledGrammar parse(Reader source) throws IOException {
        PackedDfa<GaTokenType> dfa = new GaLexer().compile();
        tokenizer = new GaTokenizer(new DfaTokenizer<>(dfa, source));
        nextToken = tokenizer.nextToken();
        while(nextToken != null) {
            switch(nextToken.getType()) {
                case IDENTIFIER:
                    addRule(rule());
                    break;
                case EXCLAMATION:
                    addIgnore(ignoreStatement());
                    break;
                default:
                    throw unexpectedTokenException();
            }
        }

        // everything collected
        final Dictionary dictionary = new Dictionary();
        List<CompiledRule> compiledRules = rules.stream()
                .flatMap(r -> r.toCompiledRules(dictionary).stream()).collect(Collectors.toList());
        Set<String> ignoredPatterns = ignored.stream().flatMap(s -> s.getIgnoredList().stream()).collect(Collectors.toSet());
        String[] nonTerminals = dictionary.nonTerminals();
        String[] terminals = dictionary.terminals();

        List<TokenDefinition<Integer>> tokenDefinitions = new ArrayList<>();
        for (int i = 0;i < terminals.length; i++) {
            String terminal = terminals[i];
            String regexp = terminal.substring(1, terminal.length() - 1);
            tokenDefinitions.add(new TokenDefinition<>(regexp, i));
        }

        for (String pattern: ignoredPatterns) {
            String regexp = pattern.substring(1, pattern.length() - 1);
            tokenDefinitions.add(new TokenDefinition<>(regexp, Integer.MAX_VALUE));
        }

        PackedDfa<Set<Integer>> packedDfa= new Lexer<Integer>(tokenDefinitions).compile();

        return new CompiledGrammar(nonTerminals, terminals, compiledRules.stream().collect(Collectors.groupingBy(CompiledRule::getTarget)), packedDfa);



    }

    private Rule rule() throws IOException {
        System.out.println("rule");
        Token<GaTokenType> target = this.nextToken;

        nextToken = tokenizer.nextToken();
        if (this.nextToken == null) {
        }
        if (nextToken == null || nextToken.getType() != GaTokenType.EQUALS) {
            throw unexpectedTokenException();
        }
        nextToken = tokenizer.nextToken();
        Choice choice = choice();
        if (nextToken == null || nextToken.getType() != GaTokenType.SEMICOLON) {
            throw unexpectedTokenException();
        }
        nextToken = tokenizer.nextToken();
        return new Rule(target.getText(), choice);
    }



    private IllegalStateException unexpectedTokenException() {
        return  nextToken == null
                ?  new IllegalStateException("Unexpected end of input")
                : new IllegalStateException("Unexpected token: "
                + nextToken.getType()
                + "(" + nextToken.getText() + ") at "
                + nextToken.getLine() + ":" +nextToken.getPos());
    }

    private IgnoreStatement ignoreStatement() throws IOException {
        System.out.println("ignore");
        List<String> ignoredPatterns = new ArrayList<>();
        nextToken = tokenizer.nextToken();
        while (nextToken != null && nextToken.getType() == GaTokenType.STRING) {
            ignoredPatterns.add(nextToken.getText());
            nextToken = tokenizer.nextToken();
        }

        if (nextToken == null || nextToken.getType() != GaTokenType.SEMICOLON) {
            throw unexpectedTokenException();
        }
        nextToken = tokenizer.nextToken();
        return new IgnoreStatement(ignoredPatterns);
    }

    private Choice choice() throws IOException {
        System.out.println("choice");
        List<Chain> chains = new ArrayList<>();
        while (true) {
            chains.add(chain());
            if (nextToken == null) {
                throw unexpectedTokenException();
            }
            if (nextToken.getType() != GaTokenType.BAR) {
                break;
            }
            nextToken = tokenizer.nextToken();
        }
        return new Choice(chains);
    }

    private Chain chain() throws IOException {
        System.out.println("chain");
        List<RuleItemsSupplier> items = new ArrayList<>();
        while(true) {
            if (nextToken == null) {
                break;
            }
            switch (nextToken.getType()) {
                case IDENTIFIER:
                    items.add(new NonTerminal(nextToken.getText()));
                    nextToken = tokenizer.nextToken();
                    break;
                case STRING:
                    items.add(new Terminal(nextToken.getText()));
                    nextToken = tokenizer.nextToken();
                    break;
                case LEFT_BRACE:
                    items.add(zeroOrMore());
                    break;
                case LEFT_BRACKET:
                    items.add(zeroOrOne());
                    break;
                case LPAREN:
                    items.add(exactlyOne());
                    break;
                default:
                    return new Chain(items);

            }
        }
        return new Chain(items);
    }

    private ZeroOrMore zeroOrMore() throws IOException {
        nextToken = tokenizer.nextToken();
        Choice choice = choice();
        if (nextToken == null || nextToken.getType() != GaTokenType.RIGHT_BRACE) {
            throw unexpectedTokenException();
        }
        nextToken = tokenizer.nextToken();
        return new ZeroOrMore(choice);
    }

    private ZeroOrOne zeroOrOne() throws IOException {
        nextToken = tokenizer.nextToken();
        Choice choice = choice();
        if (nextToken == null || nextToken.getType() != GaTokenType.RIGHT_BRACKET) {
            throw unexpectedTokenException();
        }
        nextToken = tokenizer.nextToken();
        return new ZeroOrOne(choice);
    }

    private ExactlyOne exactlyOne() throws IOException {
        nextToken = tokenizer.nextToken();
        Choice choice = choice();
        if (nextToken == null || nextToken.getType() != GaTokenType.RPAREN) {
            throw unexpectedTokenException();
        }
        nextToken = tokenizer.nextToken();
        return new ExactlyOne(choice);
    }


}
