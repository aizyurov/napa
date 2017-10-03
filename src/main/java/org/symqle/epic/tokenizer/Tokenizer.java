package org.symqle.epic.tokenizer;

import java.io.IOException;
import java.io.Reader;

/**
 * @author lvovich
 */
public class Tokenizer<T> {

    private final PackedDfa<T> dfa;
    private final Reader reader;
    private int line = 1;
    private int pos = 0;
    private int state = 0;
    private StringBuilder valueBuilder = new StringBuilder();
    private Position position = Position.REGULAR;
    int nextChar;

    public Tokenizer(final PackedDfa<T> dfa, final Reader reader) throws IOException {
        this.dfa = dfa;
        this.reader = reader;
        advance();
    }

    private void advance() throws IOException {
        nextChar = reader.read();
        switch (nextChar)  {
            case '\r':
                switch(position) {
                    case AFTER_CR: case END_OF_LINE:
                        line += 1;
                        pos = 1;
                        position = Position.AFTER_CR;
                        break;
                    default:
                        position = Position.AFTER_CR;
                        break;
                }
                break;
            case '\n':
                switch(position) {
                    case AFTER_CR:
                        position = Position.END_OF_LINE;
                        break;
                    case END_OF_LINE:
                        line += 1;
                        pos = 0;
                        break;
                    default:
                        position = Position.END_OF_LINE;
                        break;
                }
                break;
            case -1:
                // EOF
                break;
            default: {
                switch (position) {
                    case AFTER_CR: case END_OF_LINE:
                        line += 1;
                        pos = 1;
                        position = Position.REGULAR;
                        break;
                    default:
                        pos += 1;
                }
            }
        }
    }

    public Token<T> nextToken() throws IOException {
        int tokenLine = line;
        int tokenPos = pos;
        while(true) {
            if (nextChar == -1) {
                if (state == 0) {
                    return null;
                } else {
                    return constructToken(tokenLine, tokenPos);
                }
            }
            char currentChar = (char) nextChar;
            int nextState = dfa.nextState(state, currentChar);
            if (nextState >= 0) {
                valueBuilder.append(currentChar);
                advance();
                state = nextState;
            } else {
                return constructToken(tokenLine, tokenPos);
            }
        }
    }

    private Token<T> constructToken(int tokenLine, int tokenPos) {
        String value = valueBuilder.toString();
        T tag = dfa.tag(state);
        if (tag == null) {
            throw new IllegalStateException("Unexpected character " + nextChar + " at " + line + ":" + pos);
        }
        valueBuilder.setLength(0);
        state = 0;
        return new Token<>(tag, tokenLine, tokenPos, value);
    }

    private enum Position {
        REGULAR,
        AFTER_CR,
        END_OF_LINE,
    }
}
