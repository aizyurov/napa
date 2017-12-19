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
public class FieldDeclarationTest extends TestCase {
    private final Parser g;

    public FieldDeclarationTest() {
        g = JavaGrammar.getParser();
    }

    public void testSimple() throws Exception {
        SyntaxTree tree = parse("private String s;");
        Assert.assertEquals(Arrays.asList("private"),
                tree.find("FieldModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("UnannType.UnannReferenceType.UnannClassOrInterfaceType.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("s"),
                tree.find("VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));

    }

    public void testBasic() throws Exception {
        SyntaxTree tree = parse("private static final String s;");
        Assert.assertEquals(Arrays.asList("private", "static", "final"),
                tree.find("FieldModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("UnannType.UnannReferenceType.UnannClassOrInterfaceType.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("s"),
                tree.find("VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));

    }

    public void testInitialized() throws Exception {
        SyntaxTree tree = parse("private static final String s = \"some text\";");
        Assert.assertEquals(Arrays.asList("private", "static", "final"),
                tree.find("FieldModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("UnannType.UnannReferenceType.UnannClassOrInterfaceType.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("s"),
                tree.find("VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("\"some text\""),
                tree.find("VariableDeclaratorList.VariableDeclarator.VariableInitializer.Expression").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
    }

    public void testMulti() throws Exception {
        SyntaxTree tree = parse("private static final String s1, s2;");
        Assert.assertEquals(Arrays.asList("private", "static", "final"),
                tree.find("FieldModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("String"),
                tree.find("UnannType.UnannReferenceType.UnannClassOrInterfaceType.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("s1", "s2"),
                tree.find("VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));

    }

    public void testArray() throws Exception {
        SyntaxTree tree = parse("private static final int[][] s1, s2;");
        Assert.assertEquals(Arrays.asList("private", "static", "final"),
                tree.find("FieldModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("int"),
                tree.find("UnannType.UnannReferenceType.UnannArrayType.UnannPrimitiveType").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("s1", "s2"),
                tree.find("VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(2,
                tree.find("UnannType.UnannReferenceType.UnannArrayType.Dims.Dim").size());

    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("FieldDeclaration", new StringReader(source));
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
