extern int yylineno;
/* extern unsigned char yytext[]; */
extern int yylex();
#define YYSTYPE struct node *

/*main enum listing all the different types of tokens.
FILEEND is used because yylex returns 0 after lexing, so we don't want to use the 0 value for anything else */
/*enum tokens {FILEEND, RESVWORD, ID, NUMBER, CHAR, SHORT, INT, ULONG, STRING,
             PAREN, BRACKET, BRACE, STMTSEP, INCDEC, LOGNEGOP, BITWISENEGOP,
             MULOP, ADDOP, SHIFTOP, RELOP, EQUALOP, BITWISEOP, LOGICALOP,
             CONDITIONAL, ASSIGNOP, SEQUENTIALOP, DOTOP, ATOP, DOLLAROP, GRAVEOP};
*/
/*basic kinds*/
enum kinds {
        RESERVED_WORD, IDENTIFIER, OPERATOR, CONSTANT
    };

