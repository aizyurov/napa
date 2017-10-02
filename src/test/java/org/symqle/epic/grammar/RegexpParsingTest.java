package org.symqle.epic.grammar;

import junit.framework.TestCase;
import org.symqle.epic.regexp.TokenDefinition;
import org.symqle.epic.regexp.first.FirstStep;
import org.symqle.epic.regexp.first.NfaNode1;
import org.symqle.epic.regexp.model.Regexp;
import org.symqle.epic.regexp.model.RegexpSyntaxTreeBuilder;
import org.symqle.epic.regexp.scanner.Scanner;
import org.symqle.epic.regexp.second.DfaNode;
import org.symqle.epic.regexp.second.NfaNode2;
import org.symqle.epic.regexp.second.SecondStep;
import org.symqle.epic.regexp.second.ThirdStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 9/27/17.
 */
public class RegexpParsingTest extends TestCase {

    public void testSimpleSequence() {
        NfaNode1 start = new FirstStep(Collections.singletonList("\"abc\""), Collections.emptyList()).makeNfa();
        SecondStep secondStep = new SecondStep();
        Collection<NfaNode2> second = secondStep.convert(start);
        ThirdStep thirdStep = new ThirdStep();
        DfaNode startDfa = thirdStep.build(second).getNodes().get(0);
        System.out.println(startDfa.getEdges().keySet());
    }

    public void testChoice() {
        final Scanner scanner = new Scanner("\"ab|cd\"");
        final Regexp regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
        System.out.println(regexp);
    }

    public void testNoQuoteBefore() {
        final Scanner scanner = new Scanner("ab|cd\"");
        final Regexp regexp;
        try {
            regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testNoQuoteAfter() {
        final Scanner scanner = new Scanner("\"ab|cd");
        final Regexp regexp;
        try {
            regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testExtraText() {
        final Scanner scanner = new Scanner("\"ab|cd\"xyz");
        final Regexp regexp;
        try {
            regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testCharacterSet() {
        final Scanner scanner = new Scanner("\"[a-bcd]\"");
        final Regexp regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
        System.out.println(regexp);

    }

    public void testAllFeatures() {
        final Scanner scanner = new Scanner("\"(([a-bcd]+)|z*|y?)*def\"");
        final Regexp regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
        regexp.endState(new NfaNode1());

        System.out.println(regexp);

    }

    private List<String> quoteAll(List<String> patterns) {
        return patterns.stream().map(p -> '"' + p + '"').collect(Collectors.toList());
    }

    public void testFullLexis() {
        final String comment = "/[*]([^*]|[*][^/])*[*]/";
        final String whitespace = "[ \\n\\r\\t]+";
        List<String> ignored = Arrays.asList(whitespace, comment);
        String identifier = "[a-zA-Z_][a-zA-Z0-9_]*";
        String number = "[0-9]+";
        List<String> separators = Arrays.asList("[.]", ",", ";", "[+]", "-", "[*]", "/", "<", ">", "=", "==", "<=", ">=", "!=", "!");
        List<String> keywords = Arrays.asList("class", "interface", "package", "extends", "implements", "private", "public", "final", "void", "int", "long", "boolean", "char", "import", "volatile", "transient", "default");

        final List<TokenDefinition> tokenDefinitions = new ArrayList<>();
        final List<String> meaningful = new ArrayList<>();
        meaningful.addAll(keywords);
        meaningful.addAll(separators);
        meaningful.add(identifier);
        meaningful.add(number);
        final NfaNode1 startState;
        {
            final long startTs = System.currentTimeMillis();
            startState = new FirstStep(quoteAll(meaningful), quoteAll(ignored)).makeNfa();
            System.out.println("Nodes: " + NfaNode1.count() + " in " + (System.currentTimeMillis() - startTs) + " millis");
        }

        Collection<NfaNode2> nfa;
        {
            final long startTs = System.currentTimeMillis();
            nfa = new SecondStep().convert(startState);
            final NfaNode2 secondFaState = nfa.iterator().next();
            long intermideateTs = System.currentTimeMillis();
            System.out.println("NFA Nodes: " + NfaNode2.size() + " in " + (intermideateTs - startTs) + " millis");
            System.out.println("NFA Edges: " + NfaNode2.edgeCount());

//            Set<CharacterSet> allCharacterSets = new HashSet<>();
//            for (SecondFaNode node: nfa) {
//                allCharacterSets.addAll(node.getEdges().stream().map(Edge2::getCharacterSet).collect(Collectors.toSet()));
//            }
//            CharacterClassRegistry registry = new CharacterClassRegistry(allCharacterSets);
//
//            System.out.println("Character classes: " + registry.size() + " in " + (System.currentTimeMillis() - intermideateTs));
//
//            for (char c = 'a'; c < 'z'; c++) {
//                System.out.println(c + ": " + registry.classOf(c));
//            }
//            for (char c = 'A'; c < 'C'; c++) {
//                System.out.println(c + ": " + registry.classOf(c));
//            }
//            for (char c = '0'; c < '3'; c++) {
//                System.out.println(c + ": " + registry.classOf(c));
//            }
            System.out.println("=============");

        }

        {
            final long startTs = System.currentTimeMillis();
            ThirdStep thirdStep = new ThirdStep();
            DfaNode dfaNode = thirdStep.build(nfa).getNodes().get(0);
            System.out.println("DFA Nodes: " + thirdStep.size() + " in " + (System.currentTimeMillis() - startTs) + " millis");
            System.out.println("DFA Edges: " + thirdStep.edges());
        }



    }

}
