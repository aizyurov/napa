package org.symqle.epic.grammar;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.lexer.TokenDefinition;
import org.symqle.epic.lexer.build.Lexer;
import org.symqle.epic.tokenizer.DfaTokenizer;
import org.symqle.epic.tokenizer.PackedDfa;
import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader);
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
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader);
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
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        packedDfa.printStats();
        System.out.println("==================");
//        Reader reader = new StringReader("public  class  Lexer {}");
        Reader reader = new StringReader("/** comment */ public class Abc implements Def {\n   int i;\r   long j;\r\n}\n\r\nhaha");
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader);
        for (Token<Set<String>> token = tokenizer.nextToken(); token != null; token = tokenizer.nextToken()) {
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
        PackedDfa<Set<String>> packedDfa = new Lexer<>(tokenDefinitions).compile();
        packedDfa.printStats();
        System.out.println("==================");
//        Reader reader = new StringReader("public  class  Lexer {}");
        Reader reader = new StringReader("/** comment */ public class @Abc implements Def {\n   int i;\r   long j;\r\n}\n\r\nhaha");
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader);
        try {
            for (Token<Set<String>> token = tokenizer.nextToken(); token != null; token = tokenizer.nextToken()) {
                System.out.println(token);
            }
            fail("Exception expected");
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().contains("'@'"));
        }
        System.out.println("================");
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
        Tokenizer<Set<String>> tokenizer = new DfaTokenizer<>(packedDfa, reader);
        for (Token<Set<String>> token = tokenizer.nextToken(); token != null; token = tokenizer.nextToken()) {
            System.out.println(token);
        }
        System.out.println("Time: " + (System.currentTimeMillis()-startTokens));

    }

}
