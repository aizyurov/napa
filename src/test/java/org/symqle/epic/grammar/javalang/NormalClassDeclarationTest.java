package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 10/28/17.
 */
public class NormalClassDeclarationTest extends TestCase {
    private final Parser g;

    public NormalClassDeclarationTest() {
        g = JavaGrammar.getParser();
    }

    public void testItself() throws Exception {
        parse("public class NormalClassDeclarationTest extends TestCase #ClassBody#");
    }

    public void testModifiers() throws Exception {
        final SyntaxTree tree = parse("public static strictfp abstract final class NormalClassDeclarationTest extends TestCase #ClassBody#");
        Assert.assertEquals(Arrays.asList("public", "static",  "strictfp", "abstract", "final"), tree.find("ClassModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("TestCase"),
                tree.find("Superclass.ClassType.ClassOrInterfaceType.AnnotatedIdentifierWithTypeArguments.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
    }

    public void testNoModifiers() throws Exception {
        parse("class NormalClassDeclarationTest extends TestCase #ClassBody#");
    }

    public void testNoExtendsImplements() throws Exception {
        parse("public class NormalClassDeclarationTest #ClassBody#");
    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("NormalClassDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
