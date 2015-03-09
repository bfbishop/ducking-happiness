extern int yylineno;
/* extern unsigned char yytext[]; */
extern int yylex();
#define YYSTYPE struct node *

/*main enum listing all the different types of tokens.
FILEEND is used because yylex returns 0 after lexing, so we don't want to use the 0 value for anything else */
/*basic kinds*/
enum kinds {
        RESERVED_WORD, IDENTIFIER, OPERATOR, CONSTANT
    };

