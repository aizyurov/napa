package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.analyser.grammar.GaGrammar;
import org.symqle.epic.gparser.CompiledGrammar;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTreeNode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ImportTest extends TestCase {

    private final CompiledGrammar g;

    public ImportTest() throws IOException {
        g = getGrammar();
    }

    private CompiledGrammar getGrammar() throws IOException {
        return new GaGrammar().parse(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("java.napa"), "UTF-8"));
    }

    public void testSingleStatic() throws Exception {
        final String source = "import static java.util.stream.Collectors.toMap;";
        Set<SyntaxTreeNode> forest = new Parser(g).parse("ImportDeclaration", new StringReader(source), 100);
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


}
