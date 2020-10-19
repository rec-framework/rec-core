grammar RecPipe;

@header {
package net.kimleo.rec.parser;
}

start : script;

script : importDecl* process+ EOF;

importDecl : IMPORT (packageName)  SEMI_COLUMN;

packageName : ID (DOT ID)*;

process : source (PIPE processing)+ SEMI_COLUMN;

source : STDIN | stream | invokeExpression;

processing : simpleProc | invokeExpression | parallelProc;

simpleProc : STDOUT | STDERR | stream;

parallelProc : LPAREN simpleProc (COMMA simpleProc)* RPAREN;

stream : ID;

invokeExpression : ID LPAREN (value (COMMA value)* )? RPAREN;

value : ID | NUM_VALUE | STRING;


STREAM : 'stream';

STDIN : 'stdin';
STDOUT : 'stdout';
STDERR : 'stderr';

IMPORT : 'import';

PIPE : '|';
SEMI_COLUMN : ';';
COMMA : ',';
DOT : '.';

LPAREN : '(';
RPAREN : ')';

ID : [a-zA-Z]+;

NUM_VALUE : [0-9]+;

COMMENT : '#' (~'\n')* '\n' -> skip;

STRING : '"' ('""' | ~'"')* '"' ;

WS : [ \t\r\n]+ -> skip;
