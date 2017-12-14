package org.symqle.napa.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
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
public class MethodDeclarationTest extends TestCase {
    private final Parser g;

    public MethodDeclarationTest() {
        g = JavaGrammar.getParser();
    }

    public void testBasic() throws Exception {
        SyntaxTree tree = parse("private static final String s(int i, Set<String> s);");
        Assert.assertEquals(Arrays.asList("private", "static", "final"),
                tree.find("MethodModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("MethodHeader.Result.UnannType.UnannReferenceType.UnannClassOrInterfaceType.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("int", " Set<String>"),
                tree.find("MethodHeader.MethodDeclarator.FormalParameterList.FormalParameter.UnannType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));

    }

    public void testThrows() throws Exception {
        SyntaxTree tree = parse("private static final String s(int i, Set<String> s) throws IllegalStateException, java.io.IOException;");
        Assert.assertEquals(Arrays.asList("private", "static", "final"),
                tree.find("MethodModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("MethodHeader.Result.UnannType.UnannReferenceType.UnannClassOrInterfaceType.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("int", " Set<String>"),
                tree.find("MethodHeader.MethodDeclarator.FormalParameterList.FormalParameter.UnannType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));

        Assert.assertEquals(Arrays.asList(" IllegalStateException", " java.io.IOException"),
                tree.find("MethodHeader.Throws.ExceptionTypeList.ExceptionType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));
    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("MethodDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
