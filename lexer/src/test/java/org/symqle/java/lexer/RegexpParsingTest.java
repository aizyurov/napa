package org.symqle.java.lexer;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.lexer.TokenDefinition;
import org.symqle.napa.lexer.build.Lexer;
import org.symqle.napa.tokenizer.DfaTokenizer;
import org.symqle.napa.tokenizer.PackedDfa;
import org.symqle.napa.tokenizer.Token;
import org.symqle.napa.tokenizer.Tokenizer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 9/27/17.
 */
public class RegexpParsingTest extends TestCase {

    private String quote(String pattern) {
        return pattern;
    }

    public void testComment() throws Exception {
        final String comment = "/[*]([^*]|[*][^/])*[*]/";
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.add(new TokenDefinition<>(quote(comment), quote(comment)));
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        Reader reader = new StringReader("/** comment */");
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
        System.out.println(tokenizer.nextToken());
    }

    public void testControlInBrackets() throws Exception {
        final String cr = "[\n]";
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.add(new TokenDefinition<>(quote(cr), quote(cr)));
        try {
            PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
            fail("LF in brackets accepted");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testAny() throws Exception {
        final String any = ".*a";
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.add(new TokenDefinition<>(quote(any), any));
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        Reader reader = new StringReader("defa");
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
        System.out.println(tokenizer.nextToken());
    }

    public void testLongestMatch() throws Exception {
        final String gt = ">";
        final String shiftAssign = ">>=";
        final String identifier = "[a-z]";
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.add(new TokenDefinition<>(gt, gt, true));
        tokenDefinitions.add(new TokenDefinition<>(shiftAssign, shiftAssign, true));
        tokenDefinitions.add(new TokenDefinition<>(identifier, identifier, false));
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        Reader reader = new StringReader(">>>=a");
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
        System.out.println(tokenizer.nextToken());
        System.out.println(tokenizer.nextToken());
        System.out.println(tokenizer.nextToken());
        System.out.println(tokenizer.nextToken());

    }

    public void testFullLexis() throws Exception {
        final String comment = "/[*]([^*]|[*][^/])*[*]/";
        final String whitespace = "[ \\n\\r\\t]+";
        List<String> ignored = Arrays.asList(whitespace, comment);
        String identifier = "[a-zA-Z_][a-zA-Z0-9_]*";
        String number = "[0-9]+";
        List<String> separators = Arrays.asList("[.]", ",", ";", "[+]", "-", "[*]", "/", "<", ">", "=", "==", "<=", ">=", "!=", "!", "{", "}");
        List<String> keywords = Arrays.asList("class", "interface", "package", "extends", "implements", "private", "public", "final", "void", "int", "long", "boolean", "char", "import", "volatile", "transient", "default");

        final List<String> meaningful = new ArrayList<>();
        meaningful.addAll(keywords);
        meaningful.addAll(separators);
        meaningful.add(identifier);
        meaningful.add(number);
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.addAll(meaningful.stream().map(x -> new TokenDefinition<>(quote(x), quote(x))).collect(Collectors.toList()));
        tokenDefinitions.addAll(ignored.stream().map(x -> new TokenDefinition<>(quote(x), quote(x))).collect(Collectors.toList()));
        PackedDfa<Set<String>> packedDfa = null;
        for (int i = 0; i<50; i++) {
            final long startTs = System.currentTimeMillis();
            packedDfa = new Lexer<>(tokenDefinitions).compile();
            System.out.println("Lexer time: " + (System.currentTimeMillis() - startTs));
        }
        packedDfa.printStats();
        System.out.println("==================");
//        Reader reader = new StringReader("public  class  Lexer {}");
        Reader reader = new StringReader("/** comment */ public class Abc implements Def {\n   int i;\r   long j;\r\n}\n\r\nhaha");
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
        for (Token<Set<String>> token = tokenizer.nextToken(); token.getType() != null; token = tokenizer.nextToken()) {
            System.out.println(token);
        }
        System.out.println("================");
    }

    public void testUnexpected() throws Exception {
        final String comment = "/[*]([^*]|[*][^/])*[*]/";
        final String whitespace = "[ \\n\\r\\t]+";
        List<String> ignored = Arrays.asList(whitespace, comment);
        String identifier = "[a-zA-Z_][a-zA-Z0-9_]*";
        String number = "[0-9]+";
        List<String> separators = Arrays.asList("[.]", ",", ";", "[+]", "-", "<", ">", "=", "==", "<=", ">=", "!=", "!", "{", "}");
        List<String> keywords = Arrays.asList("class", "interface", "package", "extends", "implements", "private", "public", "final", "void", "int", "long", "boolean", "char", "import", "volatile", "transient", "default");

        final List<String> meaningful = new ArrayList<>();
        meaningful.addAll(keywords);
        meaningful.addAll(separators);
        meaningful.add(identifier);
        meaningful.add(number);
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.addAll(meaningful.stream().map(x -> new TokenDefinition<>(quote(x), quote(x))).collect(Collectors.toList()));
        tokenDefinitions.addAll(ignored.stream().map(x -> new TokenDefinition<>(quote(x), quote(x))).collect(Collectors.toList()));
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        packedDfa.printStats();
        // illegal character
        {
            Reader reader = new StringReader("@ abc");
            Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
            int i = 0;
            for (Token<Set<String>> token = tokenizer.nextToken(); token.getType() != null; token = tokenizer.nextToken()) {
                switch (i++) {
                    case 0:
                        assertEquals(token.getType(), Collections.emptySet());
                        break;
                    case 1:
                        assertEquals(token.getType(), Collections.singleton(whitespace));
                        break;
                    case 2:
                        assertEquals(token.getType(), Collections.singleton(identifier));
                        break;
                    default:
                        fail("Too many tokens");
                }
            }
        }
        // unclosed comment
        {
            Reader reader = new StringReader("/* abc");
            Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
            int i = 0;
            for (Token<Set<String>> token = tokenizer.nextToken(); token.getType() != null; token = tokenizer.nextToken()) {
                switch (i++) {
                    case 0:
                        assertEquals(token.getType(), Collections.emptySet());
                        break;
                    default:
                        fail("Too many tokens");
                }
                System.out.println(token);
            }
        }


    }

    public void testItself() throws Exception {
        final String comment = "/[*]([^*]|[*][^/])*[*]/";
        final String whitespace = "[ \\n\\r\\t]+";
        List<String> ignored = Arrays.asList(whitespace, comment);
        String identifier = "[a-zA-Z_][a-zA-Z0-9_]*";
        String number = "[0-9]+";
        List<String> separators = Arrays.asList("[.]", ",", ";", "[+]", "-", "[*]", "/", "<", ">", "=", "==", "<=", ">=", "!=", "!", "{", "}", "\\(", "\\)", ":", "[\\\"]");
        List<String> keywords = Arrays.asList("class", "interface", "package", "extends", "implements", "private", "public", "final", "void", "int", "long", "boolean", "char", "import", "volatile", "transient", "default");

        final List<String> meaningful = new ArrayList<>();
        meaningful.addAll(keywords);
        meaningful.addAll(separators);
        meaningful.add(identifier);
        meaningful.add(number);
        List<TokenDefinition<String>> tokenDefinitions = new ArrayList<>();
        tokenDefinitions.addAll(meaningful.stream().map(x -> new TokenDefinition<>(quote(x), quote(x))).collect(Collectors.toList()));
        tokenDefinitions.addAll(ignored.stream().map(x -> new TokenDefinition<>(quote(x), quote(x))).collect(Collectors.toList()));
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        packedDfa.printStats();
        System.out.println("==================");
//        Reader reader = new StringReader("public  class  Lexer {}");
        Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("sample.txt"), "UTF-8");
        final long startTokens = System.currentTimeMillis();
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader, Collections.emptySet());
        for (Token<Set<String>> token = tokenizer.nextToken(); token.getType() != null; token = tokenizer.nextToken()) {
            System.out.println(token);
        }
        System.out.println("Time: " + (System.currentTimeMillis()-startTokens));

    }


}
