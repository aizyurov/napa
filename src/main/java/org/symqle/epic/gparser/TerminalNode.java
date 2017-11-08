package org.symqle.epic.gparser;

import com.sun.org.apache.xalan.internal.xsltc.compiler.SyntaxTreeNode;
import org.symqle.LeafNode;
import org.symqle.epic.tokenizer.Token;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lvovich
 */
public class TerminalNode implements RawSyntaxNode {

    private final int tag;
    private final List<Token<TokenProperties>> preface;
    private final Token<TokenProperties> token;

    public TerminalNode(int tag, List<Token<TokenProperties>> preface, Token<TokenProperties> token) {
        this.tag = tag;
        this.preface = preface;
        this.token = token;
    }

    @Override
    public SyntaxTree toSyntaxTreeNode(SyntaxTree parent, CompiledGrammar grammar) {
        return new LeafNode(grammar.getTerminalName(tag), token.getText(), parent, preface.stream().map(Token::getText).collect(Collectors.toList()), token.getLine(), token.getPos());
    }

    @Override
    public int getLine() {
        return token.getLine();
    }

    @Override
    public int getPos() {
        return token.getPos();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TerminalNode that = (TerminalNode) o;

        if (tag != that.tag) return false;
        if (!token.equals(that.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tag;
        result = 31 * result + token.hashCode();
        return result;
    }
}
