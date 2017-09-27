package org.symqle.epic.grammar;

import junit.framework.TestCase;
import org.symqle.epic.regexp.Lexem;
import org.symqle.epic.regexp.first.CharacterSetRegistry;
import org.symqle.epic.regexp.first.FirstFaState;
import org.symqle.epic.regexp.first.FirstStep;
import org.symqle.epic.regexp.model.Regexp;
import org.symqle.epic.regexp.parser.RegexpSyntaxTreeBuilder;
import org.symqle.epic.regexp.scanner.Scanner;
import org.symqle.epic.regexp.second.SecondFaState;
import org.symqle.epic.regexp.second.SecondStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aizyurov on 9/27/17.
 */
public class RegexpParsingTest extends TestCase {

    public void testSimpleSequence() {
        final Scanner scanner = new Scanner("\"abc\"");
        final Regexp regexp = new RegexpSyntaxTreeBuilder(scanner).regexp();
        final FirstFaState start = new FirstFaState();
        regexp.endState(start);
        System.out.println(regexp);
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
        regexp.endState(new FirstFaState());

        System.out.println(regexp);

    }

    public void testFullLexis() {
        final String comment = "/[*]([^*]|[*][^/])*[*]/";
        final String whitespace = "[ \\n\\r\\t]+";
        List<String> ignored = Arrays.asList(whitespace, comment);
        String identifier = "[a-zA-Z_][a-zA-Z0-9_]*";
        String number = "[0-9]+";
        List<String> separators = Arrays.asList("[.]", ",", ";", "[+]", "-", "[*]", "/", "<", ">", "=", "==", "<=", ">=", "!=", "!");
        List<String> keywords = Arrays.asList("class", "interface", "package", "extends", "implements", "private", "public", "final", "void", "int", "long", "boolean", "char", "import", "volatile", "transient", "default");

        final List<Lexem> lexems = new ArrayList<>();
        final List<String> meaningful = new ArrayList<>();
        meaningful.addAll(keywords);
        meaningful.addAll(separators);
        meaningful.add(identifier);
        meaningful.add(number);
        for (String pattern: meaningful) {
            lexems.add(new Lexem('"' + pattern + '"', true));
        }
        for (String pattern: ignored) {
            lexems.add(new Lexem('"' + pattern + '"', false));
        }
        final FirstFaState startState;
        {
            final long startTs = System.currentTimeMillis();
            startState = new FirstStep(lexems).automaton();
            System.out.println("Nodes: " + FirstFaState.count() + " in " + (System.currentTimeMillis() - startTs) + " millis");
            System.out.println("Charsets: " + CharacterSetRegistry.size());
        }

        {
            final long startTs = System.currentTimeMillis();
            final SecondFaState secondFaState = new SecondStep().convert(startState);
            System.out.println("Nodes: " + SecondFaState.size() + " in " + (System.currentTimeMillis() - startTs) + " millis");
            System.out.println("Edges: " + SecondFaState.edgeCount());
        }

    }

}
