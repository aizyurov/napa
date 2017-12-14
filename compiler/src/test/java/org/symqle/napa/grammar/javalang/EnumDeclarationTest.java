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
public class EnumDeclarationTest extends TestCase {
    private final Parser g;

    public EnumDeclarationTest() {
        g = JavaGrammar.getParser();
    }

    public void testBasic() throws Exception {
        final SyntaxTree tree = parse("public static strictfp abstract final enum EnumTest implements TestCase {GOOD, BAD}");
        Assert.assertEquals(Arrays.asList("public", "static",  "strictfp", "abstract", "final"), tree.find("ClassModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("EnumTest"),
                tree.find("Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("GOOD", "BAD"),
                tree.find("EnumBody.EnumConstantList.EnumConstant").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("TestCase"),
                tree.find("Superinterfaces.InterfaceTypeList.InterfaceType.ClassOrInterfaceType.AnnotatedIdentifierWithTypeArguments.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("EnumDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
