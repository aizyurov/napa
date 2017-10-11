# epic grammar in its own notation

grammar = { ignore_statement | rule } ;

ignore_statement = "!" ignored { ignored } ";" ;

ignored = regexp ;

regexp = "\"[^\"]+\"" ;

! "#[^\n\r]*(\r|\n)" "[ \n\r\t]+" ;

rule = nonTerminal "=" choice ";" ;

choice = chain { "|" chain } ";" ;

chain = element { element } ;

element = terminal | nonTerminal | zero_or_more | zero_or_one | parenthezised ;

nonTerminal = "[a-zA-Z][a-zA-Z0-9_]*" ;

terminal = regexp ;

zero_or_more = "{" choice "}" ;

zero_or_one = "["  choice "]" ;

pernthesized = "(" choice ")" ;


