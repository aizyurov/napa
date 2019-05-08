package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.napa.parser.CompiledGrammar;
import org.symqle.napa.parser.Parser;
import org.symqle.napa.parser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 10/28/17.
 */
public class ConstructorDeclarationTest extends TestCase {
    private final CompiledGrammar g;

    public ConstructorDeclarationTest() {
        g = JavaGrammar.getGrammar();
    }

    public void testBasic() throws Exception {
        SyntaxTree tree = parse("private String(int i, Set<String> s) {}");
        Assert.assertEquals(Collections.singletonList("private"),
                tree.find("ConstructorModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("ConstructorDeclarator.SimpleTypeName.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("int", " Set<String>"),
                tree.find("ConstructorDeclarator.FormalParameterList.FormalParameter.UnannType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));

    }

    public void testThrows() throws Exception {
        SyntaxTree tree = parse("private String(int i, Set<String> s)  throws IllegalStateException, java.io.IOException {}");
        Assert.assertEquals(Collections.singletonList("private"),
                tree.find("ConstructorModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("ConstructorDeclarator.SimpleTypeName.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("int", " Set<String>"),
                tree.find("ConstructorDeclarator.FormalParameterList.FormalParameter.UnannType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList(" IllegalStateException", " java.io.IOException"),
                tree.find("Throws.ExceptionTypeList.ExceptionType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));
    }

    public void testExplicitConstructorInvocation() throws Exception {
        SyntaxTree tree = parse("private String(int i, Set<String> s) {this(i);}");
        Assert.assertEquals(Collections.singletonList("private"),
                tree.find("ConstructorModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("ConstructorDeclarator.SimpleTypeName.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("int", " Set<String>"),
                tree.find("ConstructorDeclarator.FormalParameterList.FormalParameter.UnannType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));
    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = new Parser().parse(g, "ConstructorDeclaration", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
