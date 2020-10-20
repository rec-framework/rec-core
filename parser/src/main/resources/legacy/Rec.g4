grammar Rec;

@header {
package net.kimleo.rec.parser;
}

start : model;

model : importDecl* (streamDef | processorDef | process)* EOF;

streamDef : STREAM name=ID streamContent? SEMI_COLUMN;

streamContent : EQ invokeExpression;

processorDef : PROCESSOR name=ID params processorBody;

params : LPAREN (name=ID (COMMA name=ID)*)? RPAREN;

processorBody : LBRACE processorClause RBRACE;

processorClause
    :  fromClause? skipClause? whereClause? emitClause?
    | matching ;

fromClause : FROM processing;
whereClause : WHERE condition;
skipClause : SKIP_ condition;
emitClause : EMIT selector (COMMA selector)*
    | EMIT LPAREN selector (COMMA selector)* RPAREN;


matching : MATCH LPAREN selector RPAREN LBRACE caseClause+ matchElseClause? RBRACE;

caseClause : (CASE value COLUMN (SKIP_ | emitClause));

matchElseClause : ELSE COLUMN (SKIP_ | emitClause);


condition : selector relation (selector | value);

relation : '>' | '<' | '=' | '>=' | '<=' | 'in' | '!=';

selector : (ID | IT) (DOT value)?;

importDecl : IMPORT (packageName)  SEMI_COLUMN;

packageName : ID (DOT ID)*;

process : source (PIPE processing)+ SEMI_COLUMN;

source : STDIN | ID;

processing : simpleProc | invokeExpression | parallelProc;

simpleProc : STDOUT | STDERR | stream;

parallelProc : LPAREN simpleProc (COMMA simpleProc)* RPAREN;

stream : ID;

invokeExpression : ID LPAREN (value (COMMA value)* )? RPAREN;

value : ID | NUM_VALUE | STRING;


STREAM : 'stream';

PROCESSOR : 'processor';

SKIP_ : 'skip';
EMIT : 'emit';

TRUE : 'true';
FALSE : 'false';
NIL : 'nil';

STDIN : 'stdin';
STDOUT : 'stdout';
STDERR : 'stderr';


MATCH : 'match';
CASE : 'case';
ELSE : 'else';
IMPORT : 'import';
FROM : 'from';
WHERE : 'where';

IT : 'it';

ARROW : '->';

PIPE : '|';
SEMI_COLUMN : ';';
COMMA : ',';
DOT : '.';
COLUMN : ':';

LBRACE : '{';
RBRACE : '}';

LPAREN : '(';
RPAREN : ')';

LSQUARE : '[';
RSQUARE : ']';

EQ : '=';

ID : [a-zA-Z]+;

NUM_VALUE : [0-9]+;

COMMENT : '#' (~'\n')* '\n' -> skip;

STRING : '"' ('""' | ~'"')* '"' ;

WS : [ \t\r\n]+ -> skip;
