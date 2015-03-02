#include <stdio.h>
#include "lexer.h"
#include "y.tab.h"
#include "node.h"
#include <string.h>

/*
    This is the main class for running the lexer. It provides the main method which will run lexing on the file and print out the results.
*/

extern FILE *yyin;

/**
    These arrays translate the token values into string values.
    They're currently not used since we pull the text directly from the input stream
**/
char *str_repr_tokens[] = {"EOF" /*0 is returned by yylex on end of file*/
            ,"break", "char", "continue", "do", "else", "for",
			 "goto", "if", "int", "long", "return", "short",
			 "signed", "unsigned", "void", "while", "(", ")", "[", "]", "{", "}", ";", "@", "$", "`", "++", "--", "!", "~", "*", "/", "%", "+", "-", "<<", ">>", "<", "<=", 
            ">", ">=", "==", "!=", "|", "^", "&", "||", "&&", "?", ":", 
            "=", "+=", "-=", "*=", "/=", "%=", "<<=", ">>=", "&=", "^=", 
            "|=", ",", "."};

struct node * yylval;


/**
    main - run the lexer as a standalone program. Prints out token kind, line number, and value.
    Errors will be printed to stderr containing the line number and a description of the error.

    parameter: int argc - number of command line arguments
               char *argv[] - array of strings representing the command line arguments

    return: 0 for success, non-zero otherwise

    side effects: prints lexing output and errors, calls other functions with side-effects
**/
int main(int argc, char *argv[]) {
  FILE * inputFile;
  int token;

  if (argc == 2 || argc > 3 || (argc == 3 && strcmp(argv[1], "-f"))) {
    fprintf(stderr,"Usage: ./lexer [-f FILENAME]\n");
    fprintf(stderr,"No arguments runs the lexer in test mode, using stdin\n");
    fprintf(stderr,"Otherwise supply a filename for input to the lexer\n");
    return 1;
  }

  if (argc == 3) {
    inputFile = fopen(argv[2], "r");
  } else {
    inputFile = stdin;
  }
 
  yyin = inputFile;
  token = yylex();
  while(token != 0) {
    /*printf("Line %d\n",yylineno);*/
    
    switch(token) {
    /*kinds of reserved words*/
    case RW_BREAK:
    case RW_CHAR:
    case RW_CONTINUE:
    case RW_DO:
    case RW_ELSE:
    case RW_FOR:
    case RW_GOTO:
    case RW_IF:
    case RW_INT:
    case RW_LONG:
    case RW_RETURN:
    case RW_SHORT:
    case RW_SIGNED:
    case RW_UNSIGNED:
    case RW_VOID:
    case RW_WHILE:
      printf("RESVWORD:\tline %d\tvalue %s\n", yylineno, str_repr_tokens[token]);
    break;
      /*kinds of operators*/
      /* kinds of PARENs */
    case PARENLEFT:
    case PARENRIGHT:
      /* kinds of BRACKETs */
    case BRACKETLEFT:
    case BRACKETRIGHT:
      /* kinds of BRACEs */
    case BRACELEFT:
    case BRACERIGHT:
      /* kinds of STMTSEPs */
    case SEPSEMICOLON:
      /* kinds of INCDECs */
    case INCDECINCREMENT:
    case INCDECDECREMENT:

      /* kinds of LOGNEGs */
    case LOGNEG:
      /* kinds of BITWISENEGs */
    case BITWISENEG:
      /* kinds of MULOPs */
    case MULTIPLY: 
    case DIVIDE: 
    case REMAINDER: 

      /* kinds of ADDOPs */
    case ADDITION: 
    case SUBTRACTION: 

      /* kinds of SHIFTOPs */
    case LEFT: 
    case RIGHT: 

      /* kinds of RELOPs */
    case LT: 
    case LE: 
    case GT: 
    case GE: 

      /* kinds of EQUALOPs */
    case EQ: 
    case NE: 

      /* kinds of BITWISEOPs */
    case BITOR: 
    case BITXOR: 
    case BITAND:

      /* kinds of LOGICALOPs */
    case LOGICALOR: 
    case LOGICALAND: 

      /* kinds of CONDITIONALs */
    case CONDQUEST:
    case CONDCOLON:

      /* kinds of ASSIGNOPs */
    case ASSIGNMENTSIMPLE: 
    case ASSIGNMENTADDITION: 
    case ASSIGNMENTSUBTRACTION: 
    case ASSIGNMENTMULTIPLICATION: 
    case ASSIGNMENTDIVISION: 
    case ASSIGNMENTREMAINDER: 
    case ASSIGNMENTLEFTSHIFT: 
    case ASSIGNMENTRIGHTSHIFT: 
    case ASSIGNMENTBITWISEAND: 
    case ASSIGNMENTBITWISEXOR:
    case ASSIGNMENTBITWISEOR: 

      /* kinds of SEQUENTIALOPs */
    case SEQUENTIALCOMMA: 
        printf("OPSEP:\tline %d\tvalue %s\n", yylineno, str_repr_tokens[token]);
    break;
     /* other lexemes */
    case ID:
      printf("ID:\tline %d\tvalue ", yylval->line_number);
      if(yylval->data.identifier.name == NULL) {
	    printf("***Out of memory***\n");
      } else {
	    printf("%s\n", (char *)yylval->data.identifier.name);
      }
      break;
    case CHAR:
      printf("CHAR:\tline %d\tvalue %ld\n", yylval->line_number, yylval->data.number.value);
      break;
    case INT:
      printf("INT:\tline %d\tvalue %ld\n", yylval->line_number, yylval->data.number.value);
      break;
    case ULONG:
      printf("ULONG:\tline %d\tvalue %ld\n", yylval->line_number, yylval->data.number.value);
      break;
    case STRING:
      printf("STRING:\tline %d\tvalue ", yylval->line_number);
      if(yylval->data.string.value == NULL) {
        printf("***Out of memory***\n");
      } else {
        int i;
        for (i=0; i<yylval->data.string.length; i++) {
            printf("%c",yylval->data.string.value[i]);
        }
        printf("\n");
      }
      break;
    default:
      fprintf(stderr,"Line %d: Unrecognized token\n", yylval->line_number);
      break;
    }
    token = yylex();
  }
  return 0;
}

int yywrap(void) {
  return 1;
}

