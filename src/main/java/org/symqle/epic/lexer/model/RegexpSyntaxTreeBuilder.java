package org.symqle.epic.lexer.model;

import org.symqle.epic.lexer.build.NfaBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aizyurov on 9/26/17.
 */
public class RegexpSyntaxTreeBuilder {

    private final Scanner scanner;

    public RegexpSyntaxTreeBuilder(Scanner scanner) {
        this.scanner = scanner;
    }

    public NfaBuilder regexp() {
        final Choice choice = choice();
        ensureSame(scanner.preview(), LexerTokenType.EOF);
        return new Regexp(choice);
    }

    private Choice choice() {
        List<Sequence> variants = new ArrayList<>();
        variants.add(sequence());
        while (scanner.preview() == LexerTokenType.BAR) {
            scanner.get();
            variants.add(sequence());
        }
        return new Choice(variants);
    }

    private Sequence sequence() {
        List<Repeat> repeats = new ArrayList<>();
        for (Repeat repeat = repeat(); repeat != null; repeat = repeat()) {
            repeats.add(repeat);
        }
        return new Sequence(repeats);
    }

    private Repeat repeat() {
        final Primary primary = primary();
        if (primary == null) {
            return null;
        }
        switch(scanner.preview()) {
            case STAR:
                scanner.get();
                return new Repeat(primary, Repeat.Repetitions.ZERO_OR_MORE);
            case PLUS:
                scanner.get();
                return new Repeat(primary, Repeat.Repetitions.ONE_OR_MORE);
            case QUESTION:
                scanner.get();
                return new Repeat(primary, Repeat.Repetitions.ZERO_OR_ONE);
            default:
                return new Repeat(primary, Repeat.Repetitions.EXACTLY_ONE);
        }
    }

    private Primary primary() {
        switch(scanner.preview()) {
            case CHARACTER: case MINUS: case PLUS: case STAR: case CARET: case QUESTION:
                return new CharacterPrimary(scanner.get());
            case DOT:
                scanner.get();
                return new DotPrimary();
            case LBRACKET:
                scanner.get();
                final CharSet set = set();
                ensureSame(scanner.preview(), LexerTokenType.RBRACKET);
                scanner.get();
                return new SetPrimary(set);
            case LPAREN:
                scanner.get();
                final Choice choice = choice();
                ensureSame(scanner.preview(), LexerTokenType.RPAREN);
                scanner.get();
                return new ChoicePrimary(choice);
            default:
               return null;

        }
    }

    private CharSet set() {
        if (scanner.preview() == LexerTokenType.CARET) {
            scanner.get();
            return new ExcludeSet(includeSet().characterSet());
        } else {
            return includeSet();
        }
    }

    private IncludeSet includeSet() {
        List<Range> ranges = new ArrayList<>();
        for (Range range = range(); range != null; range = range()) {
            ranges.add(range);
        }
        return new IncludeSet(ranges);
    }

    private Range range() {
        Character start = getRangeChar();
        if (start == null) {
            return null;
        }
        Character end;
        if (scanner.preview() == LexerTokenType.MINUS) {
            scanner.get();
            end = getRangeChar();
            if (end == null) {
                throw new IllegalArgumentException("No end of range");
            }
        } else {
            end = start;
        }
        return new Range(start, end);
    }

    private Character getRangeChar() {
        switch (scanner.preview()) {
            case CHARACTER:case DOT:case QUESTION:case STAR:case PLUS:case LPAREN:case RPAREN:
                return scanner.get();
            case CR:case LF:case TAB:
                throw new IllegalStateException("Illegal character in brackets: " + scanner.preview());
            default:
                return null;
        }
    }

    private void ensureSame(LexerTokenType expected, LexerTokenType actual) {
        if (expected != actual) {
            throw new IllegalArgumentException(actual + " expected " + " but " + expected + " found");
        }
    }

}
