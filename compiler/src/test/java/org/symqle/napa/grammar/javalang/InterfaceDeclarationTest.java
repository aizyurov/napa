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
public class InterfaceDeclarationTest extends TestCase {
    private final Parser g;

    public InterfaceDeclarationTest() {
        g = JavaGrammar.getParser();
    }

    public void testAll() throws Exception {
        SyntaxTree tree = parse("public interface Sample extends Foo,Bar { String ABC=\"abc\"; public void a(); default public void def(int i) {}}");
        Assert.assertEquals(Collections.singletonList("public"), tree.find("InterfaceModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("Sample"), tree.find("Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList(" Foo","Bar"), tree.find("ExtendsInterfaces.InterfaceTypeList.InterfaceType").stream().map(SyntaxTree::getSource).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("ABC"), tree.find("InterfaceBody.InterfaceMemberDeclaration.ConstantDeclaration.VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Arrays.asList("a","def"), tree.find("InterfaceBody.InterfaceMemberDeclaration.InterfaceMethodDeclaration.MethodHeader.MethodDeclarator.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("NormalInterfaceDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
