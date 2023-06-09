package org.symqle.napa.compiler.lexis;

import org.symqle.napa.lexer.TokenDefinition;
import org.symqle.napa.lexer.build.Lexer;
import org.symqle.napa.tokenizer.PackedDfa;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.symqle.napa.lexer.TokenDefinition.def;
import static org.symqle.napa.compiler.lexis.GaTokenType.*;

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
        def(":", COLON),
        def("{", LEFT_BRACE),
        def("}", RIGHT_BRACE),
        def("[\\[]", LEFT_BRACKET),
        def("[\\]]", RIGHT_BRACKET),
        def("\\(", LPAREN),
        def("\\)", RPAREN),
        def("~", TILDE),
        def("[ \\r\\n\\t]+", IGNORE),
        def("#[^\\r\\n]*[\\r\\n]", IGNORE),
        def("\"([^\"]|[\\\\][\"])+\"", STRING),
        def("'([^']|\\\\')+'", LITERAL_STRING),
        def("\\|", BAR),
        def("+", PLUS)
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
