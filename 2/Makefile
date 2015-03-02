all:
	yacc -d bparser.y
	lex lexer-standalone.lex
	gcc lex.yy.c -c
	gcc -pedantic -Wall lex.yy.o lexer.c node.c -lfl -o lexer
	gcc -pedantic -Wall y.tab.c node.c -ly -lfl -o parser

lexer:
	lex lexer-standalone.lex
	gcc lex.yy.c -c
	gcc -pedantic -Wall lex.yy.o lexer.c node.c -lfl -o lexer

clean:
	-rm -f lex.yy.c lex.yy.o
