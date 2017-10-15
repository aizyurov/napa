package org.symqle.epic.grammar;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.GrammarException;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class AnalyzerTest extends TestCase {

    public void testTrivial() throws Exception {
        String grammar = "identifier = \"[a-zA-Z][a-zA-Z0-9]*\";\n" +
                "class_declaration = \"class\" identifier \";\" ;\n" +
                "! \" +\" ;";
        CompiledGrammar g = new GaGrammar().parse(new StringReader(grammar));
        String source = "class wow ;";
        Set<SyntaxTreeNode> tree = new Parser(g, "class_declaration", new StringReader(source), 100).parse();
        Assert.assertEquals(1, tree.size());
        System.out.println(tree.iterator().next().text());

    }

    public void testUndefinedNonTerminal() throws Exception {
        String grammar = "identifier = \"[a-zA-Z][a-zA-Z0-9]*\";\n" +
                // mistype below
                "class_declaration = \"class\" identifeir \";\" ;\n" +
                "! \" +\" ;";
        try {
            CompiledGrammar g = new GaGrammar().parse(new StringReader(grammar));
            fail("GrammarException expected");
        } catch (GrammarException e) {
            Assert.assertTrue(e.getMessage().contains("identifeir"));
        }
    }

    public void testExpression() throws Exception {
        CompiledGrammar g = new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("expression.napa"), "UTF-8"));
        String source = "a + 2 * b + c/3/4";
        Set<SyntaxTreeNode> expressions = new Parser(g, "expression", new StringReader(source), 100).parse();
        Assert.assertEquals(1, expressions.size());
        SyntaxTreeNode expression = expressions.iterator().next();
        Assert.assertEquals("expression", expression.name());
        Assert.assertEquals(5, expression.children().size());
        Assert.assertEquals("a", expression.children().get(0).value());
        Assert.assertEquals("+", expression.children().get(1).value());
        SyntaxTreeNode term1 = expression.children().get(2);
        Assert.assertEquals("term", term1.name());
        Assert.assertEquals(3, term1.children().size());
        Assert.assertEquals("2", term1.children().get(0).value());
        Assert.assertEquals("*", term1.children().get(1).value());
        Assert.assertEquals("b", term1.children().get(2).value());
        Assert.assertEquals(source, expression.text());
    }

    public void testEmpty1() throws Exception {
        CompiledGrammar g = new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("empty1.napa"), "UTF-8"));
        String source = "class a;";
        Set<SyntaxTreeNode> forest = new Parser(g, "declaration", new StringReader(source), 100).parse();
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(3, tree.children().size());
        Assert.assertEquals("class", tree.children().get(0).value());
    }

    public void testEmpty2() throws Exception {
        CompiledGrammar g = new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("empty2.napa"), "UTF-8"));
        String source = "class a;";
        Set<SyntaxTreeNode> forest = new Parser(g, "declaration", new StringReader(source), 100).parse();
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(4, tree.children().size());
        Assert.assertNull(tree.children().get(0).value());
        Assert.assertEquals(source, tree.text());
    }

    public void testLeftZeroOrMore() throws Exception {
        CompiledGrammar g = new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("leftZeroOrMore.napa"), "UTF-8"));
        String source = "java.lang.String s;";
        Set<SyntaxTreeNode> forest = new Parser(g, "field_definition", new StringReader(source), 100).parse();
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(3, tree.children().size());
        SyntaxTreeNode type = tree.children().get(0);
        Assert.assertEquals("type", type.name());
        Assert.assertEquals(5, type.children().size());
        Assert.assertEquals(Arrays.asList("java", "lang", "String"), type.children().stream().filter(c -> c.name().equals("name")).map(c -> c.value()).collect(Collectors.toList()));
        Assert.assertEquals(source, tree.text());
    }

    public void testEarlyExample() throws Exception {
        String grammar = "B = A A ; A = \"x\" | \"x\" \"x\" ;";
        String source = "xxx";
        CompiledGrammar g = new GaGrammar().parse(new StringReader(grammar));
        Set<SyntaxTreeNode> forest = new Parser(g, "B", new StringReader(source), 100).parse();
        Assert.assertEquals(2, forest.size());
        for (SyntaxTreeNode tree : forest) {
            Assert.assertEquals(source, tree.text());
        }
    }

    public void testIngorable() throws Exception {
        CompiledGrammar g = new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("ignorable.napa"), "UTF-8"));
        String source = "/*comment*/class/*garbage*/abc";
        Set<SyntaxTreeNode> forest = new Parser(g, "class_definition", new StringReader(source), 100).parse();
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(3, tree.children().size());
        SyntaxTreeNode comment = tree.children().get(0);
        Assert.assertEquals("comment", comment.name());
        Assert.assertEquals("/*comment*/", comment.value());
        SyntaxTreeNode name = tree.children().get(2);
        Assert.assertEquals("name", name.name());
        Assert.assertEquals("abc", name.value());
        Assert.assertEquals(Collections.singletonList("/*garbage*/"), name.preface());
    }

}


