TokenType: QUOTE CHARACTER DOT QUESTION STAR PLUS LPAREN RPAREN LBRACKET RBRACKET CARET BAR MINUS

regexp = choice
choice = sequence { BAR sequence }
sequence = repeate { repeate }
repeate = primary [ multiplier ]
multiplier = PLUS | STAR | QUESTION
primary = CHARACTER | DOT | LBRACKET set RBRACKET | LPAREN choice RPAREN
set = include | exclude
exclude = CARET include
include = range { range }
range = end [ MINUS end ]
end = CHARACTER | DOT | PLUS | STAR | QUESTION | LPAREN | RPAREN | BAR



