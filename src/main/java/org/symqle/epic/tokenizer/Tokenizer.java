package org.symqle.epic.tokenizer;

import java.io.IOException;

/**
 * @author lvovich
 */
public interface Tokenizer<T> {
    Token<T> nextToken() throws IOException;

    public enum Position {
        REGULAR,
        AFTER_CR,
        END_OF_LINE,
    }
}
