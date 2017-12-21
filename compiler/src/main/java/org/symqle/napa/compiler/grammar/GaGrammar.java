package org.symqle.napa.compiler.grammar;

import org.symqle.napa.compiler.lexis.GaLexer;
import org.symqle.napa.compiler.lexis.GaTokenType;
import org.symqle.napa.compiler.lexis.GaTokenizer;
import org.symqle.napa.gparser.Assembler;
import org.symqle.napa.parser.CompiledGrammar;
import org.symqle.napa.gparser.CompiledRule;
import org.symqle.napa.parser.GrammarException;
import org.symqle.napa.parser.NonTerminalNode;
import org.symqle.napa.parser.RawSyntaxNode;
import org.symqle.napa.parser.SyntaxTree;
import org.symqle.napa.parser.TerminalNode;
import org.symqle.napa.parser.TokenProperties;
import org.symqle.napa.lexer.TokenDefinition;
import org.symqle.napa.lexer.build.Lexer;
import org.symqle.napa.tokenizer.DfaTokenizer;
import org.symqle.napa.tokenizer.PackedDfa;
import org.symqle.napa.tokenizer.Token;
import org.symqle.napa.tokenizer.Tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other;

/**
 * @author lvovich
 */
public class GaGrammar {

    private Token<GaTokenType> nextToken;
    private Tokenizer<GaTokenType> tokenizer;

    public CompiledGrammar compile(Reader source) throws IOException {
        final long beforeStart = System.currentTimeMillis();
        try {
            RawSyntaxNode rawSyntaxNode = parse(source);
            SyntaxTree tree = rawSyntaxNode.toSyntaxTreeNode(null);
            final Dictionary dictionary = new Dictionary();

            List<SyntaxTree> patterns = tree.find("grammar/patternDef");
            Map<String, List<String>> dependencies = new HashMap<>();
            Map<String, SyntaxTree> patternMap = new HashMap<>();
            for (SyntaxTree pattern: patterns) {
                String name = pattern.getChildren().get(0).getValue();
                SyntaxTree existing = patternMap.put(name, pattern);
                if (existing != null) {
                    throw new GrammarException("Duplicate definition of " + name + " at " + pattern.getLine() + ":" + pattern.getPos() + ", defined at " + existing.getLine() + existing.getPos());
                }
                dependencies.put(name, pattern.find("expression/IDENFIFIER").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
            }
            TSort<String> tsort = new TSort<>();
            for (String dependent: dependencies.keySet()) {
                tsort.add(dependent, dependencies.get(dependent));
            }
            List<String> sortedPatterns = tsort.sort();
            for (String name: sortedPatterns) {
                SyntaxTree pattern = patternMap.get(name);
                if (pattern == null) {
                    // skip undefined patterns here; will detect later
                    continue;
                }
                SyntaxTree expression = pattern.find("expression").get(0);
                String expressionValue = calculateExpression(expression, dictionary);
                dictionary.registerPattern(name, expressionValue);
            }

            Set<String> ignoredPatterns = new HashSet<>();
            for (SyntaxTree ignored: tree.find("grammar/ignore/expression")) {
                String regexp = calculateExpression(ignored, dictionary);
                ignoredPatterns.add(regexp);
                dictionary.registerRegexp(regexp);
            }

            Set<String> usedPatterns = new HashSet<>();
            List<CompiledRule> compiledRules;

            for (SyntaxTree target: tree.find("grammar/rule/IDENTIFIER")) {
            }

            for (SyntaxTree rule: tree.find("grammar/rule")) {
                SyntaxTree targetNode = rule.getChildren().get(0);
                int target = dictionary.registerNonTerminal(targetNode.getValue());
                rule.getChildren()
            }

            Set<Integer> ignorableTags;
            List<TokenDefinition<Integer>> tokenDefinitions;

            PackedDfa<Set<Integer>> packedDfa= new Lexer<Integer>(tokenDefinitions).compile();
            PackedDfa<TokenProperties> napaDfa = packedDfa.transform(s -> {
                boolean ignoreOnly = s.equals(ignoredTagSet);
                Set<Integer> difference = new HashSet<Integer>(s);
                difference.retainAll(ignorableTags);
                boolean ignorable = !difference.isEmpty();
                return new TokenProperties(ignoreOnly, ignorable, s);
            });
            System.err.println("Lexer time: " + (System.currentTimeMillis() - startTs));
            napaDfa.printStats();

            return new Assembler(dictionary.nonTerminals(), dictionary.terminals(), compiledRules, napaDfa).assemble();
        } finally {
            System.err.println("Grammar compiled in " + (System.currentTimeMillis() - beforeStart));
        }
    }

    private String calculateExpression(final SyntaxTree expression, final Dictionary dictionary) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        for (SyntaxTree child: expression.getChildren()) {
            if (child.getName().equals("STRING")) {
                stringBuilder.append(normalize(child.getValue()));
            } else {
                String value = dictionary.pattern(child.getValue());
                if (value == null) {
                    throw new GrammarException("Undefined symbol " + child.getName() + " at " + child.getLine() + ":" + child.getPos());
                }
                stringBuilder.append(normalize(value));
            }
        }
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    private RawSyntaxNode parse(Reader source) throws IOException {
        PackedDfa<GaTokenType> dfa = new GaLexer().compile();
        tokenizer = new GaTokenizer(new DfaTokenizer<>(dfa, source, GaTokenType.ERROR));
        nextToken = tokenizer.nextToken();
        return grammar();
    }

    private RawSyntaxNode grammar() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        while (nextToken.getType() != null) {
            if (nextToken.getType() == GaTokenType.TILDE) {
                children.add(ignore());
            } else {
                children.add(definition());
            }
        }
        return new NonTerminalNode(0, "grammar", children);
    }

    private RawSyntaxNode ignore() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.TILDE));
        children.add(expression());
        children.add(takeToken(GaTokenType.SEMICOLON));
        return new NonTerminalNode(0, "ignore", children);
    }

    private TerminalNode<GaTokenType> takeToken(GaTokenType expected, GaTokenType... other) throws IOException {
        Token<GaTokenType> token = take(expected, other);
        return new TerminalNode<>(0, token.getType().toString(), Collections.emptyList(), token);
    }

    private RawSyntaxNode expression() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(fragment());
        while (nextToken.getType() == GaTokenType.PLUS) {
            children.add(takeToken(GaTokenType.PLUS));
            children.add(fragment());
        }
        return new NonTerminalNode(0, "expression", children);
    }

    private RawSyntaxNode fragment() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.IDENTIFIER, GaTokenType.STRING));
        return new NonTerminalNode(0, "fragment", children);
    }

    private RawSyntaxNode definition() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.IDENTIFIER));
        switch (nextToken.getType()) {
            case EQUALS:
                children.add(expression());
                children.add(takeToken(GaTokenType.SEMICOLON));
                return new NonTerminalNode(0, "patternDef", children);
            case COLON:
                children.add(choice());
                children.add(takeToken(GaTokenType.SEMICOLON));
                return new NonTerminalNode(0, "rule", children);
            default:
                throw unexpectedTokenException();
        }
    }

    private Token<GaTokenType> take(GaTokenType expected, GaTokenType... other) throws IOException {
        Token<GaTokenType> token = this.nextToken;
        if (token.getType() == expected) {
            nextToken = tokenizer.nextToken();
            return token;
        }
        for (GaTokenType tokenType: other) {
            if (token.getType() == tokenType) {
                nextToken = tokenizer.nextToken();
                return token;
            }
        }
        throw unexpectedTokenException();
    }

    private String normalize(final String terminal) {
        return terminal.substring(1, terminal.length() - 1);
//        StringBuilder builder = new StringBuilder(terminal.length());
//        boolean afterBackslash = false;
//        for (int i = 1; i < terminal.length() - 1; i++) {
//            char next = terminal.charAt(i);
//            if (afterBackslash) {
//                if (next == '\'' || next == '"') {
//                    builder.append(next);
//                } else {
//                    builder.append('\\').append(next);
//                }
//                afterBackslash = false;
//            } else {
//                if (next == '\\') {
//                    afterBackslash = true;
//                } else {
//                    builder.append(next);
//                }
//            }
//        }
//        return builder.toString();
    }

    private GrammarException unexpectedTokenException() {
        return  nextToken == null
                ?  new GrammarException("Unexpected end of input")
                : new GrammarException("Unexpected token: "
                + nextToken.getType()
                + "(" + nextToken.getText() + ") at "
                + nextToken.getLine() + ":" +nextToken.getPos());
    }

    private IgnoreStatement ignoreStatement() throws IOException {
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

    private RawSyntaxNode choice() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        while (true) {
            children.add(chain());
            if (nextToken.getType() != GaTokenType.BAR) {
                break;
            }
            children.add(takeToken(GaTokenType.BAR));
        }
        return new NonTerminalNode(0, "choice", children);
    }

    private RawSyntaxNode chain() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        while(nextToken.getType() != null) {
            switch (nextToken.getType()) {
                case IDENTIFIER: case STRING: case LITERAL_STRING:
                    children.add(takeToken(nextToken.getType()));
                    break;
                case LEFT_BRACE:
                    children.add(zeroOrMore());
                    break;
                case LEFT_BRACKET:
                    children.add(zeroOrOne());
                    break;
                case LPAREN:
                    children.add(exactlyOne());
                    break;
                default:
                    return new NonTerminalNode(0, "chain", children);

            }
        }
        return new NonTerminalNode(0, "chain", children);
    }

    private RawSyntaxNode zeroOrMore() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.LEFT_BRACE));
        children.add(choice());
        children.add(takeToken(GaTokenType.RIGHT_BRACE));
        return new NonTerminalNode(0, "zeroOrMore", children);
    }

    private RawSyntaxNode zeroOrOne() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.LEFT_BRACKET));
        children.add(choice());
        children.add(takeToken(GaTokenType.RIGHT_BRACKET));
        return new NonTerminalNode(0, "zeroOrMore", children);
    }

    private RawSyntaxNode exactlyOne() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.LPAREN));
        children.add(choice());
        children.add(takeToken(GaTokenType.RPAREN));
        return new NonTerminalNode(0, "exactlyOne", children);
    }


}
