package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ImportTest extends TestCase {

    private final Parser g;

    public ImportTest() throws IOException {
        g = JavaGrammar.getParser();
    }

    public void testSingleClass() throws Exception {
        final String source = "import java.util.stream.Collectors;";
        List<SyntaxTree> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        Assert.assertEquals(1, tree.getChildren().size());
        final SyntaxTree singleStatic = tree.getChildren().get(0);
        Assert.assertEquals("SingleTypeImportDeclaration", singleStatic.getName());
        final SyntaxTree typeName = singleStatic.getChildren().get(1);
        Assert.assertEquals("TypeName", typeName.getName());
        final SyntaxTree ambiguousName = typeName.getChildren().get(0);
        final List<String> typeParts = ambiguousName.getChildren().stream().filter(n -> n.getName().equals("Identifier")).map(SyntaxTree::getValue).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream", "Collectors"), typeParts);
    }

    public void testOnDemandType() throws Exception {
        final String source = "import java.util.stream.*;";
        System.out.println("Before parse");
        List<SyntaxTree> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        System.out.println("After parse");
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        Assert.assertEquals(1, tree.getChildren().size());
        final SyntaxTree onDemand = tree.getChildren().get(0);
        Assert.assertEquals("TypeImportOnDemandDeclaration", onDemand.getName());
        final SyntaxTree typeName = onDemand.getChildren().get(1);
        Assert.assertEquals("PackageOrTypeName", typeName.getName());
        final SyntaxTree ambiguousName = typeName.getChildren().get(0);
        final List<String> typeParts = ambiguousName.getChildren().stream().filter(n -> n.getName().equals("Identifier")).map(SyntaxTree::getValue).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream"), typeParts);
    }

    public void testSingleStatic() throws Exception {
        final String source = "import static java.util.stream.Collectors.toMap;";
        List<SyntaxTree> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        Assert.assertEquals(1, tree.getChildren().size());
        final SyntaxTree singleStatic = tree.getChildren().get(0);
        Assert.assertEquals("SingleStaticImportDeclaration", singleStatic.getName());
        final SyntaxTree typeName = singleStatic.getChildren().get(2);
        Assert.assertEquals("TypeName", typeName.getName());
        final SyntaxTree ambiguousName = typeName.getChildren().get(0);
        final List<String> typeParts = ambiguousName.getChildren().stream().filter(n -> n.getName().equals("Identifier")).map(SyntaxTree::getValue).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream", "Collectors"), typeParts);
        final SyntaxTree identifier = singleStatic.getChildren().get(4);
        Assert.assertEquals("toMap", identifier.getValue());
    }


    public void testStaticOnDemand() throws Exception {
        final String source = "import static java.util.stream.Collectors.*;";
        List<SyntaxTree> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        Assert.assertEquals(1, tree.getChildren().size());
        final SyntaxTree staticImport = tree.getChildren().get(0);
        Assert.assertEquals("StaticImportOnDemandDeclaration", staticImport.getName());
        final SyntaxTree typeName = staticImport.getChildren().get(2);
        Assert.assertEquals("TypeName", typeName.getName());
        final SyntaxTree ambiguousName = typeName.getChildren().get(0);
        final List<String> typeParts = ambiguousName.getChildren().stream().filter(n -> n.getName().equals("Identifier")).map(SyntaxTree::getValue).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream", "Collectors"), typeParts);
        final SyntaxTree asterisk = staticImport.getChildren().get(4);
        Assert.assertEquals("*", asterisk.getValue());
    }

}
