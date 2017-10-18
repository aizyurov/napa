package org.symqle.epic.analyser.lexis;

import org.symqle.epic.lexer.TokenDefinition;
import org.symqle.epic.lexer.build.Lexer;
import org.symqle.epic.tokenizer.PackedDfa;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.symqle.epic.lexer.TokenDefinition.def;
import static org.symqle.epic.analyser.lexis.GaTokenType.*;

/**
 * Created by aizyurov on 10/4/17.
 */
public class GaLexer {

//    SEMICOLON,
//    EQUALS,
//    LEFT_BRACE,
//    RIGHT_BRACE,
//    LEFT_BRACKET,
//    RIGHT_BRACKET,
//    EXCLAMATION
    private final List<TokenDefinition<GaTokenType>> LEXEMS = Arrays.asList(
        def("[a-zA-Z][a-zA-Z0-9_]*", IDENTIFIER),
        def(";", SEMICOLON),
        def("=", EQUALS),
        def("{", LEFT_BRACE),
        def("}", RIGHT_BRACE),
        def("[\\[]", LEFT_BRACKET),
        def("[\\]]", RIGHT_BRACKET),
        def("\\(", LPAREN),
        def("\\)", RPAREN),
        def("!", EXCLAMATION),
        def("[ \\r\\n\\t]+", IGNORE),
        def("#[^\\r\\n]*[\\r\\n]", IGNORE),
        def("\"[^\"]+\"", STRING),
        def("'[^']+'", STRING),
        def("\\|", BAR)
    );

    public PackedDfa<GaTokenType> compile() {
        final PackedDfa<Set<GaTokenType>> rawDfa = new Lexer<>(LEXEMS).compile();
        return rawDfa.transform(this::select);

    }

    private GaTokenType select(Set<GaTokenType> set) {
        if (set.size() == 1) {
            return set.iterator().next();
        } else {
            throw new IllegalStateException("Multiple tags: " + set);
        }
    }
}
