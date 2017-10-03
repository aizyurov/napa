package org.symqle.epic.regexp.model;

/**
 * @author lvovich
 */
public enum LexerTokenType {
    QUOTE,
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
    EOF;

    public static LexerTokenType of(char c) {
        switch (c) {
            case '"':
                return QUOTE;
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
            case '\r':
                return CR;
            case '\t':
                return TAB;
            default:
                return CHARACTER;
        }
    }
}
