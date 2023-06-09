package org.symqle.napa.tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvovich
 */
public class DfaTokenizer<T> implements Tokenizer<T> {

    private final PackedDfa<T> dfa;
    private final Reader reader;
    private int line = 1;
    private int pos = 1;
    private int state = 0;
    private Position position = Position.REGULAR;
    private List<AttributedCharacter> buffer = new ArrayList<>();
    private List<QueuedCharacter> queue = new ArrayList<>();
    private final T errorTokenType;

    public DfaTokenizer(final PackedDfa<T> dfa, final Reader reader, final T errorTokenType) throws IOException {
        this.dfa = dfa;
        this.reader = reader;
        this.errorTokenType = errorTokenType;
    }

    private AttributedCharacter readChar() throws IOException {
        if (!buffer.isEmpty()) {
            return buffer.remove(0);
        }
        int nextChar = reader.read();
        AttributedCharacter character = new AttributedCharacter(nextChar, line, pos);
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
                        pos = 1;
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
        return character;
    }

    @Override
    public Token<T> nextToken() throws IOException {
        AttributedCharacter attributedCharacter = readChar();
        while(true) {
            final int nextChar = attributedCharacter.getCharacter();
            if (nextChar == -1) {
                if (state == 0) {
                    return new Token<>(null, line, pos, null);
                } else {
                    return constructToken();
                }
            }
            char currentChar = (char) nextChar;
            int nextState = dfa.nextState(state, currentChar);
            if (nextState >= 0) {
                queue.add(new QueuedCharacter(attributedCharacter, dfa.tag(nextState)));
                attributedCharacter = readChar();
                state = nextState;
            } else {
                if (state == 0) {
                    // the next character is inacceptable; consume it as error token
                    queue.add(new QueuedCharacter(attributedCharacter, errorTokenType));
                    return constructToken();
                } else {
                    final Token<T> token = constructToken();
                    buffer.add(attributedCharacter);
                    return token;
                }
            }
        }
    }

    private Token<T> constructToken() {
        int acceptedIndex = -1;
        for (int i = queue.size() - 1; i >= 0; i--) {
            if (queue.get(i).getTag() != null) {
                acceptedIndex = i;
                break;
            }
        }
        final T tag;
        final int endIndex;
        if (acceptedIndex < 0) {
            tag = errorTokenType;
            // eat all the queue
            endIndex = queue.size() - 1;
        } else {
            tag = queue.get(acceptedIndex).getTag();
            endIndex = acceptedIndex;
        }
        StringBuilder stringBuilder = new StringBuilder(endIndex + 1);
        final int tokenLine = queue.get(0).getCharacter().getLine();
        final int tokenPos = queue.get(0).getCharacter().getPos();
        for (int i =0; i<= endIndex; i++) {
            stringBuilder.append((char) queue.get(0).getCharacter().getCharacter());
            queue.remove(0);
        }
        state = 0;
        // put back to buffer remaining characters if any
        buffer.addAll(queue.stream().map(QueuedCharacter::getCharacter).collect(Collectors.toList()));
        queue.clear();
        return new Token<>(tag, tokenLine, tokenPos, stringBuilder.toString());
    }

    private class AttributedCharacter {
        final int character;
        final int line;
        final int pos;

        private AttributedCharacter(int character, int line, int pos) {
            this.character = character;
            this.line = line;
            this.pos = pos;
        }

        public int getCharacter() {
            return character;
        }

        public int getLine() {
            return line;
        }

        public int getPos() {
            return pos;
        }
    }

    private class QueuedCharacter {
        private final AttributedCharacter character;
        private final T tag;

        private QueuedCharacter(AttributedCharacter character, T tag) {
            this.character = character;
            this.tag = tag;
        }

        public AttributedCharacter getCharacter() {
            return character;
        }

        public T getTag() {
            return tag;
        }
    }

}
