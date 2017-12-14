package org.symqle.epic.grammar.javalang;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.symqle.epic.gparser.Parser;
import org.symqle.epic.gparser.SyntaxTree;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aizyurov on 10/28/17.
 */
public class AnnotationDeclarationTest extends TestCase {
    private final Parser g;

    public AnnotationDeclarationTest() {
        g = JavaGrammar.getParser();
    }

    public void testAll() throws Exception {
        SyntaxTree tree = parse("public @interface Sample { String ABC=\"abc\"; public String value();}");
        Assert.assertEquals(Collections.singletonList("public"), tree.find("InterfaceModifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("Sample"), tree.find("Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("ABC"), tree.find("AnnotationTypeBody.AnnotationTypeMemberDeclaration.ConstantDeclaration.VariableDeclaratorList.VariableDeclarator.VariableDeclaratorId").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
        Assert.assertEquals(Collections.singletonList("value"), tree.find("AnnotationTypeBody.AnnotationTypeMemberDeclaration.AnnotationTypeElementDeclaration.Identifier").stream().map(SyntaxTree::getValue).collect(Collectors.toList()));
    }

    private SyntaxTree parse(final String source) throws IOException {
        List<SyntaxTree> forest = g.parse("AnnotationTypeDeclaration", new StringReader(source), 100);
        Assert.assertEquals(1, forest.size());
        SyntaxTree tree = forest.iterator().next();
        Assert.assertEquals(source, tree.getSource());
        return tree;
    }

}
