package org.symqle.napa.parser;

import org.symqle.napa.tokenizer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class TerminalNode implements RawSyntaxNode {

    private final int tag;
    private final String name;
    private final List<Token<TokenProperties>> preface;
    private final Token<TokenProperties> token;

    public TerminalNode(final int tag, final String name, final List<Token<TokenProperties>> preface, final Token<TokenProperties> token) {
        this.tag = tag;
        this.name = name;
        this.preface = preface;
        this.token = token;
    }

    @Override
    public SyntaxTree toSyntaxTreeNode(SyntaxTree parent) {
        return new LeafNode(name, token.getText(), parent, preface.stream().map(Token::getText).collect(Collectors.toList()), token.getLine(), token.getPos());
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
