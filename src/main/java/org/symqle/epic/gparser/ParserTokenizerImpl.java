package org.symqle.epic.gparser;

import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public class ParserTokenizerImpl implements ParserTokenizer {

    private final Tokenizer<Set<Integer>> delegate;

    private final Set<Integer> ignored = Collections.singleton(Integer.MAX_VALUE);

    public ParserTokenizerImpl(final Tokenizer<Set<Integer>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public ParserToken nextToken() throws IOException {
        List<String> preface = new ArrayList<>();
        Token<Set<Integer>> nextToken = delegate.nextToken();
        while (nextToken != null && nextToken.getType().equals(ignored)) {
            preface.add(nextToken.getText());
            nextToken = delegate.nextToken();
        }
        return nextToken == null? null : new ParserTokenImpl(nextToken.getType(), preface, nextToken.getText(), nextToken.getLine(), nextToken.getPos());
    }
}
