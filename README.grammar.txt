# epic grammar in its own notation

grammar = { ignore | rule | patternDef } ;

ignore_statement = "~" ignored ";" ;

ignored = expression ;

expression = fragment { '+' fragment } ;

fragment = regexp | identifier ;

regexp = "\"[^\"\n\r]+\"" ;

literal = "'[^'\n\r]'";

# each identifier may appear only once with patternDef or multiple times with ruleDef, but not both

patternDef = identifier '=' expression ;

rule = identifier ':' choice ';' ;

~ "#[^\n\r]*(\r|\n)" ; # comment
~ "[ \n\r\t]+" ; #whitespace

choice = chain { "|" chain } ";" ;

chain = { identifier | regexp | literal | zero_or_more | zero_or_one | parenthezised } ;
# identifier may be pattern id or nonTerminal.

nonTerminal = "[a-zA-Z][a-zA-Z0-9_]*" ;

terminal = regexp | literal ;

zero_or_more = "{" choice "}" ;

zero_or_one = "["  choice "]" ;

parenthesized = "(" choice ")" ;


