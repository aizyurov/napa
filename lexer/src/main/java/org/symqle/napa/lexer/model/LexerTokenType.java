package org.symqle.napa.lexer.model;

/**
 * @author lvovich
 */
enum LexerTokenType {
    CHARACTER,
    DOT,
    QUESTION,
    STAR,
    PLUS,
    LPAREN,
    RPAREN,
    LBRACKET,
    RBRACKET,
    CARET,
    BAR,
    MINUS,
    CR,
    LF,
    TAB,
    FF,
    EOF;

    public static LexerTokenType of(char c) {
        switch (c) {
            case '.':
                return DOT;
            case '?':
                return QUESTION;
            case '*':
                return STAR;
            case '+':
                return PLUS;
            case '(':
                return LPAREN;
            case ')':
                return RPAREN;
            case '[':
                return LBRACKET;
            case ']':
                return RBRACKET;
            case '^':
                return CARET;
            case '|':
                return BAR;
            case '-':
                return MINUS;
            case '\n':
                return LF;
            case '\f':
                return FF;
            case '\r':
                return CR;
            case '\t':
                return TAB;
            default:
                return CHARACTER;
        }
    }
}
