package org.symqle.napa.compiler.grammar;

import org.symqle.napa.compiler.lexis.GaLexer;
import org.symqle.napa.compiler.lexis.GaTokenType;
import org.symqle.napa.compiler.lexis.GaTokenizer;
import org.symqle.napa.lexer.TokenDefinition;
import org.symqle.napa.lexer.build.Lexer;
import org.symqle.napa.parser.*;
import org.symqle.napa.tokenizer.*;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class NapaCompiler {

    private Token<GaTokenType> nextToken;
    private Tokenizer<GaTokenType> tokenizer;

    public CompiledGrammar compile(Reader source) throws IOException {
        final long beforeStart = System.currentTimeMillis();
        try {
            RawSyntaxNode rawSyntaxNode = parse(source);
            SyntaxTree tree = rawSyntaxNode.toSyntaxTreeNode(null);
            System.out.println("Parse grammar: "  + (System.currentTimeMillis() - beforeStart));
            final Dictionary dictionary = new Dictionary();

            List<SyntaxTree> patterns = tree.find("patternDef");
            Map<String, List<String>> dependencies = new HashMap<>();
            Map<String, SyntaxTree> patternMap = new HashMap<>();
            for (SyntaxTree pattern: patterns) {
                String name = pattern.getChildren().get(0).getValue();
                SyntaxTree existing = patternMap.put(name, pattern);
                if (existing != null) {
                    throw new GrammarException("Duplicate definition of " + name + " at " + pattern.getLine() + ":" + pattern.getPos() + ", defined at " + existing.getLine() + existing.getPos());
                }
                dependencies.put(name, pattern.find("expression.IDENFIFIER").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
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

            Set<Integer> ignorableTags = new HashSet<>();
            for (SyntaxTree ignored: tree.find("ignore.expression")) {
                String regexp = calculateExpression(ignored, dictionary);
                ignorableTags.add(dictionary.registerRegexp(regexp));
            }

            List<CompiledRule> compiledRules = new ArrayList<>();

            Set<Integer> usedTags = new HashSet<>();
            List<SyntaxTree> ruleTrees = tree.find("rule");
            // first register all nonTerminals
            for (SyntaxTree rule: ruleTrees) {
                SyntaxTree targetNode = rule.getChildren().get(0);
                int target = dictionary.registerNonTerminal(targetNode.getValue());
            }
            for (SyntaxTree rule: ruleTrees) {
                List<SyntaxTree> chains = rule.find("choice.chain");
                for (SyntaxTree chain: chains) {
                    SyntaxTree targetNode = rule.getChildren().get(0);
                    int target = dictionary.getNonTerminal(targetNode.getValue()); // should never be null
                    List<RuleItem> items = chainToItems(chain, dictionary, usedTags);
                    CompiledRule compiledRule = new CompiledRule(target, items);
                    compiledRules.add(compiledRule);
                }
            }

            List<TokenDefinition<Integer>> tokenDefinitions = new ArrayList<>();

            String[] terminals = dictionary.terminals();
            for (int i = 0; i < terminals.length; i++) {
                String regexp = terminals[i];
                tokenDefinitions.add(new TokenDefinition<Integer>(normalize(regexp), i, regexp.startsWith("'")));
            }

            System.out.println("Prepare compilation: "  + (System.currentTimeMillis() - beforeStart));
            final long beforeLexer = System.currentTimeMillis();
            PackedDfa<Set<Integer>> packedDfa = new Lexer<Integer>(tokenDefinitions).compile();
            PackedDfa<TokenProperties> napaDfa = packedDfa.transform(s -> {
                Set<Integer> usedDiff = new HashSet<Integer>(usedTags);
                usedDiff.retainAll(s);
                boolean ignoreOnly = usedDiff.isEmpty();
                Set<Integer> difference = new HashSet<Integer>(s);
                difference.retainAll(ignorableTags);
                boolean ignorable = !difference.isEmpty();
                return new TokenProperties(ignoreOnly, ignorable, s);
            });
            napaDfa.printStats();
            System.out.println("Lexer time: "  + (System.currentTimeMillis() - beforeLexer));
            return new Assembler(dictionary.nonTerminals(), compiledRules, napaDfa).assemble();
        } finally {
            System.err.println("Grammar compiled in " + (System.currentTimeMillis() - beforeStart));
        }
    }

    private List<RuleItem> chainToItems(final SyntaxTree chain, Dictionary dictionary, final Set<Integer> usedTags) {
        List<RuleItem> items = chain.getChildren().stream().map(e -> elementToRuleItem(e, dictionary, usedTags)).collect(Collectors.toList());
        for (RuleItem item: items) {
            if (item.getType() == RuleItemType.TERMINAL) {
                usedTags.add(item.getValue());
            }
        }
        return items;
    }

    private RuleItem elementToRuleItem(SyntaxTree element, Dictionary dictionary, Set<Integer> usedTags) {
        String value = element.getValue();
        String name = element.getName();
        switch (name) {
            case "IDENTIFIER":
                String pattern = dictionary.pattern(value);
                if (pattern != null) {
                    return new TerminalItem(dictionary.registerRegexp(pattern), value);
                }
                Integer nonTerminal = dictionary.getNonTerminal(value);
                if (nonTerminal == null) {
                    throw new GrammarException("Undefined symbol " + value + " at " + element.getLine() + ":" + element.getPos());
                }
                return new NonTerminalItem(nonTerminal);
            case "STRING": case "LITERAL_STRING":
                return new TerminalItem(dictionary.registerRegexp(value), value);
            case "zeroOrMore":
                SyntaxTree zeroOrMore = element.find("choice").get(0);
                return new ZeroOrMoreItem(choiceToRuleItemLists(zeroOrMore, dictionary, usedTags));
            case "zeroOrOne":
                SyntaxTree zeroOrOne = element.find("choice").get(0);
                return new ZeroOrOneItem(choiceToRuleItemLists(zeroOrOne, dictionary, usedTags));
            case "exactlyOne":
                SyntaxTree exactlyOne = element.find("choice").get(0);
                return new ChoiceItem(choiceToRuleItemLists(exactlyOne, dictionary, usedTags));
            default:
                throw new IllegalStateException("Unexpected element: " + name + " at " + element.getLine() + ":" + element.getPos());
        }
    }

    List<List<RuleItem>> choiceToRuleItemLists(SyntaxTree choice, Dictionary dictionary, Set<Integer> usedTags) {
        return choice.find("chain").stream().map(c -> chainToItems(c, dictionary, usedTags)).collect(Collectors.toList());
    }

    private String calculateExpression(final SyntaxTree expression, final Dictionary dictionary) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        for (SyntaxTree fragment: expression.getChildren()) {
            if (fragment.getName().equals("PLUS")) {
                continue;
            }
            SyntaxTree child = fragment.getChildren().get(0);
            if (child.getName().equals("STRING")) {
                stringBuilder.append(normalize(child.getValue()));
            } else {
                String value = dictionary.pattern(child.getValue());
                if (value == null) {
                    throw new GrammarException("Undefined symbol " + fragment.getValue() + " at " + fragment.getLine() + ":" + fragment.getPos());
                }
                stringBuilder.append(normalize(value));
            }
        }
        stringBuilder.append("\"");
        return stringBuilder.toString();
    }

    private final PackedDfa<GaTokenType> napaDfa = new GaLexer().compile();

    private RawSyntaxNode parse(Reader source) throws IOException {
        tokenizer = new AsyncTokenizer<>(new GaTokenizer(new DfaTokenizer<>(napaDfa, source, GaTokenType.ERROR)));
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
        return createNonTerminalNode("grammar", children);
    }

    private RawSyntaxNode ignore() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.TILDE));
        children.add(expression());
        children.add(takeToken(GaTokenType.SEMICOLON));
        return createNonTerminalNode("ignore", children);
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
        return createNonTerminalNode("expression", children);
    }

    private RawSyntaxNode fragment() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.IDENTIFIER, GaTokenType.STRING));
        return createNonTerminalNode("fragment", children);
    }

    private RawSyntaxNode definition() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.IDENTIFIER));
        switch (nextToken.getType()) {
            case EQUALS:
                children.add(takeToken(GaTokenType.EQUALS));
                children.add(expression());
                children.add(takeToken(GaTokenType.SEMICOLON));
                return createNonTerminalNode("patternDef", children);
            case COLON:
                children.add(takeToken(GaTokenType.COLON));
                children.add(choice());
                children.add(takeToken(GaTokenType.SEMICOLON));
                return createNonTerminalNode("rule", children);
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

    private RawSyntaxNode choice() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        while (true) {
            children.add(chain());
            if (nextToken.getType() != GaTokenType.BAR) {
                break;
            }
            children.add(takeToken(GaTokenType.BAR));
        }
        return createNonTerminalNode("choice", children);
    }

    private RawSyntaxNode chain() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        String chain = "chain";
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
                    return createNonTerminalNode(chain, children);

            }
        }
        return createNonTerminalNode(chain, children);
    }

    private RawSyntaxNode createNonTerminalNode(final String name, final List<RawSyntaxNode> children) {
        return children.isEmpty() ? new EmptyNode(0, name, 0, 0) : new NonTerminalNode(0, name, children);
    }

    private RawSyntaxNode zeroOrMore() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.LEFT_BRACE));
        children.add(choice());
        children.add(takeToken(GaTokenType.RIGHT_BRACE));
        return createNonTerminalNode("zeroOrMore", children);
    }

    private RawSyntaxNode zeroOrOne() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.LEFT_BRACKET));
        children.add(choice());
        children.add(takeToken(GaTokenType.RIGHT_BRACKET));
        return createNonTerminalNode("zeroOrOne", children);
    }

    private RawSyntaxNode exactlyOne() throws IOException {
        List<RawSyntaxNode> children = new ArrayList<>();
        children.add(takeToken(GaTokenType.LPAREN));
        children.add(choice());
        children.add(takeToken(GaTokenType.RPAREN));
        return createNonTerminalNode("exactlyOne", children);
    }


}
