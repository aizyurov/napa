package org.symqle.napa.tokenizer;

import java.io.IOException;

/**
 * @author lvovich
 */
public interface Tokenizer<T> {
    Token<T> nextToken() throws IOException;

    enum Position {
        REGULAR,
        AFTER_CR,
        END_OF_LINE,
    }
}
