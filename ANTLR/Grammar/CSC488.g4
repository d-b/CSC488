/*
 * ANTLR4 CSC488 Grammar
 *
 * - Daniel Bloemendal
 */

grammar CSC488;

//
// Grammar
//

program
    : scope EOF
    ;

scope
    : '{' declaration* statement* '}'
    ;

declaration
    : 'var' variablenames (',' variablenames)* ':' type
    | functionHead scope
    | procedureHead scope
    | 'forward' functionHead
    | 'forward' procedureHead
    ;

functionHead
    : 'func' functionname '(' parameters? ')' ':' type
    ;

procedureHead
    : 'proc' procedurename '(' parameters? ')'
    ;

parameters
    : parameter (',' parameter)*
    ;

parameter
    : parametername ':' type
    ;

variablenames
    : variablename
    | variablename '[' bound ']'
    | variablename '[' bound ',' bound ']'
    ;

bound
    : INTCONST
    | generalBound '.' '.' generalBound
    ;

generalBound
    : INTCONST
    | '-' INTCONST
    ;

type
    : 'integer'
    | 'boolean'
    ;

statement
    : variable ':' '=' expression
    | 'if' expression 'then' statement* 'fi'
    | 'if' expression 'then' statement* 'else' statement* 'fi'
    | 'while' expression 'do' statement* 'end'
    | 'repeat' statement* 'until' expression
    | 'exit'
    | 'exit' 'when' expression
    | 'result' expression
    | 'return'
    | 'put' output (',' output)*
    | 'get' input (',' input)*
    | procedurename '(' arguments? ')'
    | scope
    ;

variable
    : variablename
    | parametername
    | arrayname '[' expression ']'
    | arrayname '[' expression ',' expression ']'
    ;

output
    : expression
    | 'newline'
    | text
    ;

input
    : variable
    ;

text
    : STRING
    ;

arguments
    : expression (',' expression)*
    ;

expression
    : or_expr
    ;

or_expr
    : and_expr ('or' and_expr)*
    ;

and_expr
    : not_expr ('and' not_expr)*
    ;

not_expr
    : 'not'? pred_expr
    ;

pred_expr
    : add_expr '=' add_expr
    | add_expr 'not' '=' add_expr
    | add_expr '<' add_expr
    | add_expr '<' '=' add_expr
    | add_expr '>' add_expr
    | add_expr '>' '=' add_expr
    | add_expr
    ;

add_expr
    : mul_expr (('+'|'-') mul_expr)*
    ;

mul_expr
    : unr_expr (('*'|'/') unr_expr)*
    ;

unr_expr
    : '-'? term_expr 
    ;

term_expr
    : INTCONST
    | variable
    | functionname '(' arguments? ')'
    | '(' expression ')'
    | '(' expression '?' expression ':' expression ')'
    ;

variablename  : IDENT;
arrayname     : IDENT;
functionname  : IDENT;
parametername : IDENT;
procedurename : IDENT;

//
// Lexer
//

L_PAREN   : '(';
R_PAREN   : ')';
L_SQUARE  : '[';
R_SQUARE  : ']';
L_CURLEY  : '{';
R_CURLEY  : '}';
GREATER   : '>';
LESS      : '<';
PLUS      : '+';
MINUS     : '-';
TIMES     : '*';
DIVIDE    : '/';
EQUAL     : '=';
DOT       : '.';
COMMA     : ',';
COLON     : ':';
QUESTION  : '?';
NOT       : 'not';
AND       : 'and';
OR        : 'or';

INTEGER   : 'integer';
BOOLEAN   : 'boolean';
PROCEDURE : 'proc';
FUNC      : 'func';

DO        : 'do';
ELSE      : 'else';
END       : 'end';
EXIT      : 'exit';
FI        : 'fi';
FORWARD   : 'forward';
GET       : 'get';
IF        : 'if';
PUT       : 'put';
REPEAT    : 'repeat';
RESULT    : 'result';
RETURN    : 'return';
NEWLINE   : 'newline';
THEN      : 'then';
WHEN      : 'when';
WHILE     : 'while';
UNTIL     : 'until';
VAR       : 'var';

IDENT     : ('a'..'z'|'A'..'Z') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;    
INTCONST  : '0'..'9'+;
STRING    :  '"' (~'"')* '"';

COMMENT   : '%' ~[\r\n]* '\r'? '\n' -> skip;
WS        : [ \t\r\n]+ -> skip;
