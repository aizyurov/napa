TokenType: QUOTE CHARACTER DOT QUESTION STAR PLUS LPAREN RPAREN LBRACKET RBRACKET CARET BAR MINUS

regexp = QUOTE choice QUOTE
choice = sequence { BAR sequence }
sequence = repeate { repeate }
repeate = primary [ multiplier ]
multiplier = PLUS | STAR | QUESTION
primary = CHARACTER | DOT | set | LPAREN choice RPAREN
set = LBRACKET include RBRACKET | LBRACKET exclude RBRACKET
exclude = CARET include
include = range { range }
range = end { MINUS end }
end = CHARACTER | DOT | PLUS | STAR | QUESTION | LPAREN | RPAREN | BAR



