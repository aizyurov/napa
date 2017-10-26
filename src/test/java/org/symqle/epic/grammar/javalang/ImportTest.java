package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
        Set<SyntaxTreeNode> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.text());
        Assert.assertEquals(1, tree.children().size());
        final SyntaxTreeNode singleStatic = tree.children().get(0);
        Assert.assertEquals("SingleTypeImportDeclaration", singleStatic.name());
        final SyntaxTreeNode typeName = singleStatic.children().get(1);
        Assert.assertEquals("TypeName", typeName.name());
        final List<String> typeParts = typeName.children().stream().filter(n -> n.name().equals("Identifier")).map(SyntaxTreeNode::value).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream", "Collectors"), typeParts);
    }

    public void testOnDemandType() throws Exception {
        final String source = "import java.util.stream.*;";
        System.out.println("Before parse");
        Set<SyntaxTreeNode> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        System.out.println("After parse");
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.text());
        Assert.assertEquals(1, tree.children().size());
        final SyntaxTreeNode onDemand = tree.children().get(0);
        Assert.assertEquals("TypeImportOnDemandDeclaration", onDemand.name());
        final SyntaxTreeNode typeName = onDemand.children().get(1);
        Assert.assertEquals("PackageOrTypeName", typeName.name());
        final List<String> typeParts = typeName.children().stream().filter(n -> n.name().equals("Identifier")).map(SyntaxTreeNode::value).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream"), typeParts);
    }

    public void testSingleStatic() throws Exception {
        final String source = "import static java.util.stream.Collectors.toMap;";
        Set<SyntaxTreeNode> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.text());
        Assert.assertEquals(1, tree.children().size());
        final SyntaxTreeNode singleStatic = tree.children().get(0);
        Assert.assertEquals("SingleStaticImportDeclaration", singleStatic.name());
        final SyntaxTreeNode typeName = singleStatic.children().get(2);
        Assert.assertEquals("TypeName", typeName.name());
        final List<String> typeParts = typeName.children().stream().filter(n -> n.name().equals("Identifier")).map(SyntaxTreeNode::value).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream", "Collectors"), typeParts);
        final SyntaxTreeNode identifier = singleStatic.children().get(4);
        Assert.assertEquals("toMap", identifier.value());
    }


    public void testStaticOnDemand() throws Exception {
        final String source = "import static java.util.stream.Collectors.*;";
        Set<SyntaxTreeNode> forest = g.parse("ImportDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTreeNode tree = forest.iterator().next();
        Assert.assertEquals(source, tree.text());
        Assert.assertEquals(1, tree.children().size());
        final SyntaxTreeNode staticImport = tree.children().get(0);
        Assert.assertEquals("StaticImportOnDemandDeclaration", staticImport.name());
        final SyntaxTreeNode typeName = staticImport.children().get(2);
        Assert.assertEquals("TypeName", typeName.name());
        final List<String> typeParts = typeName.children().stream().filter(n -> n.name().equals("Identifier")).map(SyntaxTreeNode::value).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("java", "util", "stream", "Collectors"), typeParts);
        final SyntaxTreeNode asterisk = staticImport.children().get(4);
        Assert.assertEquals("*", asterisk.value());
    }

}
