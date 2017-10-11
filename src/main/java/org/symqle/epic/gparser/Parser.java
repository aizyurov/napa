package org.symqle.epic.gparser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lvovich
 */
public class Parser {

    private final CompiledGrammar grammar;
    private final ParserTokenizer tokenizer;
    private final int target;
    private final int complexityLimit;

    public Parser(final CompiledGrammar grammar, final String target, final ParserTokenizer tokenizer, final int complexityLimit) {
        this.grammar = grammar;
        this.tokenizer = tokenizer;
        this.target = grammar.findNonTerminalByName(target).orElseThrow(() -> new IllegalArgumentException("NonTerminal not found: " + grammar));
        this.complexityLimit = complexityLimit;
    }

    public Set<SyntaxTreeNode> parse() {
        final ChartNode startNode = new ChartNode(-1, Collections.singletonList(new NonTerminalItem(target)), 0, null, Collections.emptyList());
        workSet.add(startNode);
        while (true) {
            while (!workSet.isEmpty()) {
                if (workSet.size() > complexityLimit) {
                    throw new IllegalStateException("Too ambiguous or too complex to parse");
                }
                final ChartNode next = workSet.iterator().next();
                workSet.remove(next);
                switch (next.availableAction()) {
                    case SHIFT:
                        shiftCandidates.add(next);
                        break;
                    case REDUCE:
                        workSet.addAll(next.reduce(grammar));
                        break;
                    case PREDICT:
                        workSet.addAll(next.predict(grammar));
                        break;
                    case EXPAND:
                        workSet.addAll(next.expand());
                        break;
                    case ACCEPT:
                        syntaxTreeCandidates.add(next.accept());
                        break;
                    default:
                        throw new IllegalStateException("Should never get here");
                }
            }
            // now shift
            final ParserToken nextToken = tokenizer.nextToken();
            if (nextToken == null) {
                return syntaxTreeCandidates;
            }
            while (!shiftCandidates.isEmpty()) {
                final ChartNode next = shiftCandidates.iterator().next();
                shiftCandidates.remove(next);
                workSet.addAll(next.shift(nextToken, grammar));
            }
            if (workSet.isEmpty()) {
                throw new IllegalStateException("Unrecognized input " + nextToken.text() + " at " + nextToken.line() +":" + nextToken.pos());
            }
        }

    }

    private final Set<ChartNode> workSet = new HashSet<>();

    private final Set<ChartNode> shiftCandidates = new HashSet<>();

    private final Set<SyntaxTreeNode> syntaxTreeCandidates = new HashSet<>();








}
