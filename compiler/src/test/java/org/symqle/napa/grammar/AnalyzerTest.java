package org.symqle.napa.grammar;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.compiler.grammar.NapaCompiler;
import org.symqle.napa.parser.CompiledGrammar;
import org.symqle.napa.parser.GrammarException;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class AnalyzerTest extends TestCase {

    public void testTrivial() throws Exception {
        String grammar = "identifier = \"[a-zA-Z][a-zA-Z0-9]*\";\n" +
                "class_declaration : \"class\" identifier \";\" ;\n" +
                "~ \" +\" ;";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        String source = "class wow ;";
        List<SyntaxTree> tree = new Parser().parse(g, "class_declaration", new StringReader(source));
        Assert.assertEquals(1, tree.size());
        System.out.println(tree.iterator().next().getSource());

    }

    public void testUndefinedNonTerminal() throws Exception {
        String grammar = "identifier = \"[a-zA-Z][a-zA-Z0-9]*\";\n" +
                // mistype below
                "class_declaration : \"class\" identifeir \";\" ;\n" +
                "~ \" +\" ;";
        try {
            CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException e) {
            System.out.printf(e.getMessage());
            Assert.assertTrue(e.getMessage().contains("identifeir"));
        }
    }

    public void testExpression() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("expression.napa"), "UTF-8"));
        String source = "a + 2 * b + c/3/4";
        List<SyntaxTree> expressions = new Parser().parse(g, "expression", new StringReader(source));
        Assert.assertEquals(1, expressions.size());
        SyntaxTree expression = expressions.iterator().next();
        Assert.assertEquals("expression", expression.getName());
        Assert.assertEquals(5, expression.getChildren().size());
        Assert.assertEquals("a", expression.getChildren().get(0).getValue());
        Assert.assertEquals("+", expression.getChildren().get(1).getValue());
        SyntaxTree term1 = expression.getChildren().get(2);
        Assert.assertEquals("term", term1.getName());
        Assert.assertEquals(3, term1.getChildren().size());
        Assert.assertEquals("2", term1.getChildren().get(0).getValue());
        Assert.assertEquals("*", term1.getChildren().get(1).getValue());
        Assert.assertEquals("b", term1.getChildren().get(2).getValue());
        Assert.assertEquals(source, expression.getSource());
    }

    public void testEmpty1() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("empty1.napa"), "UTF-8"));
        String source = "class a;";
        List<SyntaxTree> forest = new Parser().parse(g, "declaration", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(3, tree.getChildren().size());
        Assert.assertEquals("class", tree.getChildren().get(0).getValue());
    }

    public void testEmpty2() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("empty2.napa"), "UTF-8"));
        String source = "class a;";
        List<SyntaxTree> forest = new Parser().parse(g, "declaration", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(4, tree.getChildren().size());
        Assert.assertNull(tree.getChildren().get(0).getValue());
        Assert.assertEquals(source, tree.getSource());
    }

    public void testLeftZeroOrMore() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("leftZeroOrMore.napa"), "UTF-8"));
        String source = "java.lang.String s;";
        List<SyntaxTree> forest = new Parser().parse(g, "field_definition", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(3, tree.getChildren().size());
        SyntaxTree type = tree.getChildren().get(0);
        Assert.assertEquals("type", type.getName());
        Assert.assertEquals(5, type.getChildren().size());
        Assert.assertEquals(Arrays.asList("java", "lang", "String"), type.getChildren().stream().filter(c -> c.getName().equals("name")).map(c -> c.getValue()).collect(Collectors.toList()));
        Assert.assertEquals(source, tree.getSource());
    }

    public void testEarlyExample() throws Exception {
        String grammar = "B : A A ; A : \"x\" | \"x\" \"x\" ;";
        String source = "xxx";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        List<SyntaxTree> forest = new Parser().parse(g, "B", new StringReader(source));
        Assert.assertEquals(2, forest.size());
        for (SyntaxTree tree : forest) {
            Assert.assertEquals(source, tree.getSource());
        }
    }

    public void testNestedZeroOrMore() throws Exception {
        String grammar = "A : {{'a'}};";
        String source = "a";
        try {
            CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException expected) {
            Assert.assertTrue(expected.getMessage().startsWith("Infinite recursion"));
        }
    }

    public void testLeftRecursion() throws Exception {
        String grammar = "T : A | 'a'; A : 'b' | A 'b' ;";
        String source = "a";
        try {
            CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException expected) {
            Assert.assertTrue(expected.getMessage().startsWith("Infinite recursion"));
            System.out.println(expected.getMessage());
        }
    }

    public void testHiddenRecursion() throws Exception {
        String grammar = "T : A | 'a'; A : 'b' | {'x'} B ; B : ['c'] A 'd'; ";
        String source = "a";
        try {
            CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException expected) {
            Assert.assertTrue(expected.getMessage().startsWith("Infinite recursion"));
            System.out.println(expected.getMessage());
        }
    }

    public void testEmptyParentheses() throws Exception {
        String grammar = "A : 'a' () 'b' ;";
        String source = "ab";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        List<SyntaxTree> forest = new Parser().parse(g, "A", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        tree.print(System.out);
    }

    public void testInfiniteEmpty() throws Exception {
        String grammar = "A : {B} 'a' ; B : 'b' | ;";
        String source = "a";
        CompiledGrammar g = null;
        try {
            g = new NapaCompiler().compile(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException expected) {
            System.out.println(expected.getMessage());
        }
    }

    public void testEmptyBrackets() throws Exception {
        String grammar = "A : 'a' [] 'b' ;";
        String source = "ab";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        List<SyntaxTree> forest = new Parser().parse(g, "A", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        tree.print(System.out);
    }

    public void testEmptyBraces() throws Exception {
        String grammar = "A : 'a' {} 'b' ;";
        String source = "ab";
        try {
            CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException expected) {
            Assert.assertTrue(expected.getMessage().startsWith("Infinite recursion"));
            System.out.println(expected.getMessage());
        }
    }


    public void testEmptyInTheEnd() throws Exception {
        String grammar = "T : 'a' B; B : 'b' | ;";
        String source = "a";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        List<SyntaxTree> forest = new Parser().parse(g, "T", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        tree.print(System.out);
    }

    public void testEmptyInTheEnd2() throws Exception {
        String grammar = "T : 'a' ['b']; ";
        String source = "a";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        List<SyntaxTree> forest = new Parser().parse(g, "T", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        tree.print(System.out);
    }

    public void testEmptyInTheEnd3() throws Exception {
        String grammar = "T : 'a' B; B : ['b']; ";
        String source = "a";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        List<SyntaxTree> forest = new Parser().parse(g, "T", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.get(0);
        tree.print(System.out);
    }

    public void testLalrFail() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("lalr_fail.napa")));
        String source = "bed";
        List<SyntaxTree> forest = new Parser().parse(g, "S", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(3, tree.getChildren().size());
        Assert.assertEquals("E", tree.getChildren().get(1).getName());

    }

    public void testTheVeryEndDecides() throws Exception {
        String grammar = "T : {A} 'x' | {B} 'y'; A : 'a'; B : 'a' ;";
        String source = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaay";
        CompiledGrammar g = new NapaCompiler().compile(new StringReader(grammar));
        final Parser parser = new Parser();
        List<SyntaxTree> forest = parser.parse(g, "T", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals("B", tree.getChildren().get(0).getName());
        System.out.println("Tree size: " + tree.treeSize());
        System.out.println(parser.stats());

    }

    public void testIgnoreConflict() throws Exception {
        CompiledGrammar g = null;
        g = new NapaCompiler().compile(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ignorable.napa"), "UTF-8"));
        String source = "/*abc*/ class class";
        List<SyntaxTree> forest = new Parser().parse(g, "class_definition", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source,tree.getSource());
    }

    public void testPackratFailure() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new StringReader("S : 'x' S 'x' | 'x' ;"));
        String source = "xxx";
        List<SyntaxTree> forest = new Parser().parse(g, "S", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source,tree.getSource());
        tree.print(System.out);
    }

    public void testFirstFirstConflict() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new StringReader("S : E | E 'a' ;\n" +
                " E : 'b' | ;"));
        for (String source : Arrays.asList("a", "b", "ba")) {
            List<SyntaxTree> forest = new Parser().parse(g, "S", new StringReader(source));
            Assert.assertEquals(1, forest.size());
            SyntaxTree tree = forest.iterator().next();
            Assert.assertEquals(source, tree.getSource());
            tree.print(System.out);
        }

    }

    public void testFirstFollowConflict() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new StringReader("S : A 'a' 'b' ;\n" +
                " A : 'a' | ;"));
        for (String source : Arrays.asList("ab", "aab")) {
            List<SyntaxTree> forest = new Parser().parse(g, "S", new StringReader(source));
            Assert.assertEquals(1, forest.size());
            SyntaxTree tree = forest.iterator().next();
            Assert.assertEquals(source, tree.getSource());
            tree.print(System.out);
        }

    }

    public void testManyAmbuguities() throws Exception {
        CompiledGrammar g = new NapaCompiler().compile(new StringReader("S : {A|B} ;\n" +
                " A : 'a' ; B : 'a' ; "));
        String source = "aaaaaaa";
        Parser parser = new Parser();
        List<SyntaxTree> forest = parser.parse(g, "S", new StringReader(source));
        Assert.assertEquals(128, forest.size());
        System.out.println(parser.stats());

    }

}


