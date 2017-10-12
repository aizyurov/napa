package org.symqle.epic.analyser.lexis;

import org.symqle.epic.tokenizer.Token;
import org.symqle.epic.tokenizer.Tokenizer;

import java.io.IOException;

/**
 * @author lvovich
 */
public class GaTokenizer implements Tokenizer<GaTokenType> {

    private final Tokenizer<GaTokenType> delegate;

    public GaTokenizer(final Tokenizer<GaTokenType> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Token<GaTokenType> nextToken() throws IOException {
        Token<GaTokenType> next = delegate.nextToken();
        while (next != null && next.getType() == GaTokenType.IGNORE) {
            next = delegate.nextToken();
        }
        return next;
    }
}
