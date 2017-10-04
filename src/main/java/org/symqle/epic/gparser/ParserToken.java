package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class ParserToken {

    private final List<Token<Set<Integer>>> tokens;

    public ParserToken(final List<Token<Set<Integer>>> tokens) {
        this.tokens = tokens;
    }

    public boolean matches(int tag) {
        return lastToken().getType().contains(tag);
    }

    private Token<Set<Integer>> lastToken() {
        return tokens.get(tokens.size() - 1);
    }

    public List<String> preface() {
        return tokens.subList(1, tokens.size()).stream().map(Token::getText).collect(Collectors.toList());
    }

    public String text() {
        return lastToken().getText();
    }

    public int line() {
        return lastToken().getLine();
    }

    public int pos() {
        return lastToken().getPos();
    }


}
