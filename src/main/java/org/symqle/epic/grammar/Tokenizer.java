package org.symqle.epic.grammar;

import java.io.IOException;
import java.io.Reader;


/**
 * @author lvovich
 */
public class Tokenizer {

    private final Reader reader;

    private Token nextToken;

    private enum State {
        START,
        TOKEN,
        IGNORED,
        REGEXP,
        LITERAL,
        STATE,
        NT,
        WHITESPACE

    }

    private State state = State.START;

    private StringBuilder builder = new StringBuilder();

    int nextChar;

    public Tokenizer(final Reader reader) throws IOException {
        this.reader = reader;
        nextChar = readNextChar();
        nextToken = readToken();
    }

    /**
     * Take next token if its name matches
     * @param name
     * @return token if match, null if no match
     */
    public Token take(final TokenType name) throws IOException {
        Token nextToken = this.nextToken;
        if (nextToken.getName().equals(name)) {
            this.nextToken = readToken();
        }
        return nextToken;
    }

    private Token readToken() throws IOException {
        int line = this.line;
        int pos = this.pos;
        // return inside
        while (true) {
            switch(state) {
                case START:
                    if (nextChar == -1) {
                        return new Token(TokenType.EOF, "", pos, line);
                    } else if (nextChar == '$') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        state = State.IGNORED;
                    } else if (nextChar == '"') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        state = State.REGEXP;
                    } else if (nextChar == '\'') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        state = State.LITERAL;
                    } else if (nextChar == '@') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        state = State.STATE;
                    } else if (nextChar >= 'A' && nextChar <= 'Z') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        state = State.TOKEN;
                    } else if (nextChar >= 'a' && nextChar <= 'z') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        state = State.NT;
                    } else if (nextChar == ';') {
                        builder.append((char) nextChar);
                        int tokenPos = pos;
                        int tokenLine = line;
                        pos = this.pos;
                        line = this.line;
                        nextChar = readNextChar();
                        state = State.START;
                        String value = builder.toString();
                        builder.setLength(0);
                        return new Token(TokenType.SEMICOLON, value, tokenPos, tokenLine);
                    } else if (nextChar == '=') {
                        builder.append((char) nextChar);
                        int tokenPos = pos;
                        int tokenLine = line;
                        pos = this.pos;
                        line = this.line;
                        nextChar = readNextChar();
                        state = State.START;
                        String value = builder.toString();
                        builder.setLength(0);
                        return new Token(TokenType.EQ, value, tokenPos, tokenLine);
                    } else if (nextChar == '|') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        int tokenPos = pos;
                        int tokenLine = line;
                        pos = this.pos;
                        line = this.line;
                        state = State.START;
                        String value = builder.toString();
                        builder.setLength(0);
                        return new Token(TokenType.BAR, value, tokenPos, tokenLine);
                    } else if (nextChar  == ' ' || nextChar == '\n' || nextChar == '\t' || nextChar == '\r' || nextChar == '\f') {
                        state = State.WHITESPACE;
                        nextChar = readNextChar();
                    } else {
                        throw new IllegalArgumentException("Unexpected character: " + (char) nextChar);
                    }
                    break;
                case IGNORED: case TOKEN: case STATE: case NT:
                    if (nextChar >= 'A' && nextChar <= 'Z' || nextChar >= 'a' && nextChar <= 'z' || nextChar >= '0' && nextChar <= '9') {
                        builder.append((char) nextChar); // state remains the same
                        nextChar = readNextChar();
                    } else {
                        int tokenPos = pos;
                        int tokenLine = line;
                        pos = this.pos;
                        line = this.line;
                        String value = builder.toString();
                        String name = state.toString();
                        builder.setLength(0);
                        state = State.START;
                        return new Token(TokenType.valueOf(name), value, tokenPos, tokenLine);
                    }
                    break;
                case LITERAL:
                    if (nextChar == '\\') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        if (nextChar == '\n' || nextChar == -1) {
                            throw new IllegalArgumentException("Unclosed literal");
                        } else {
                            builder.append((char) nextChar);
                            nextChar = readNextChar();
                        }
                    } else if (nextChar == '\'') {
                        builder.append((char) nextChar);
                        int tokenPos = pos;
                        int tokenLine = line;
                        pos = this.pos;
                        line = this.line;
                        nextChar = readNextChar();
                        String value = builder.toString();
                        builder.setLength(0);
                        state = State.START;
                        return new Token(TokenType.LITERAL, value, tokenPos, tokenLine);
                    } else if (nextChar == -1 || nextChar == '\n' || nextChar == '\r') {
                        throw new IllegalArgumentException("Unclosed literal at " + line + ":" + pos);
                    } else {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                    }
                    break;
                case REGEXP:
                    if (nextChar == '\\') {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                        if (nextChar == '\n' || nextChar == -1) {
                            throw new IllegalArgumentException("Unclosed string");
                        } else {
                            builder.append((char) nextChar);
                            nextChar = readNextChar();
                        }
                    } else if (nextChar == '\"') {
                        builder.append((char) nextChar);
                        int tokenPos = pos;
                        int tokenLine = line;
                        pos = this.pos;
                        line = this.line;
                        nextChar = readNextChar();
                        String value = builder.toString();
                        builder.setLength(0);
                        state = State.START;
                        return new Token(TokenType.REGEXP, value, tokenPos, tokenLine);
                    } else if (nextChar == -1 || nextChar == '\n' || nextChar == '\r') {
                        throw new IllegalArgumentException("Unclosed regexp at " + line + ":" + pos);
                    } else {
                        builder.append((char) nextChar);
                        nextChar = readNextChar();
                    }
                    break;
                case WHITESPACE:
                    if (nextChar  == ' ' || nextChar == '\n' || nextChar == '\t' || nextChar == '\r' || nextChar == '\f') {
                        nextChar = readNextChar();
                    } else {
                        line = this.line;
                        pos = this.pos;
                        state = State.START;
                    }
                    break;
                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    private int readNextChar() throws IOException {
        int nextChar = reader.read();
        if (previousChar == '\r' && nextChar != '\n' || previousChar == '\n') {
            line +=1;
            pos = 0;
        } else if (previousChar != -1) {
            pos += 1;
        }
        previousChar = nextChar;
        return nextChar;
    }

    private int previousChar = 0;
    private int pos = 0;
    private int line = 0;

}
