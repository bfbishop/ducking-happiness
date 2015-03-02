%option yylineno


%{
/*
    This file contains the input to lex. 
*/
#include "lexer.h"
#include "y.tab.h"
#include "node.h"
/*#define DEBUG 1*/

  /* tmpstr = temporary string to be built up from the different 
     kinds of characters (escaped, non-escaped) into a full string token
     
     tmpstrlen = the length of the temporary string. Will be used 
     to track the size of the full string in the case of strings with nulls in the middle
  
     int myerrno (not listed here) is used throughout to capture errors encountered in functions
  */
  char * tmpstr;
  int tmpstrlen;
  void *malloc_id(void);
  long int eval_num(void);
    enum yytokentype eval_num_type(void);
    char eval_char_case_1(void);
    char eval_char_case_2(void);
    char eval_char_case_3(void);
  void *malloc_string_no_content(void);
  void *malloc_string(void);
    char * mystrcat (char * dest, char * src, int destLength, int srcLength);
    int myerrno;


%}

/*Comment recognition adopted from flex official documentation
http://flex.sourceforge.net/manual/How-can-I-match-C_002dstyle-comments_003f.html
*/
%x IN_COMMENT IN_STRING IN_CHAR

%option nounput
%option noinput

/* regular definitions */
delim   [ \t\n\f\v]
ws      {delim}+
letter  [A-Za-z]
digit   [0-9]
octal   [0-7]
escape  \\(n|t|b|r|f|v|\\|\'|\"|a|\?)
underscore _
id      ({letter}|{underscore})({letter}|{digit}|{underscore})*
/*have to handle the case of multiple 0 in a row. Need error since we don't have octal here*/
zero    0
error_zero 00+
number  [1-9]{digit}*
char_case1  [^\n"\\]
char_case2  \\{octal}{1,3}
char_case3  {escape}
any_char .
bad_id  {digit}+{id}
%%

{bad_id}    { fprintf(stderr,"Line %d: no integer suffixes or digit-leading identifiers allowed (%s)\n", yylineno, yytext);}
{ws}    {/* no action and no return */}

\/\*    BEGIN(IN_COMMENT);
<IN_COMMENT>{
"*/"      BEGIN(INITIAL);
[^*]+   /* eat comment in chunks*/
"*"       /* eat the lone star*/
<<EOF>> { BEGIN(INITIAL) ; 
        /*yylineno exceeds length of file when using EOF*/
        fprintf(stderr,"Line %d: unterminated comment (/*)\n",yylineno-1); }
}
break   {printf("%s\n",yytext); return RW_BREAK;}
char    {printf("%s\n",yytext); return RW_CHAR;}
continue {printf("%s\n",yytext); return RW_CONTINUE;}
do      {printf("%s\n",yytext); return RW_DO;}
else    {printf("%s\n",yytext); return RW_ELSE;}
for     {printf("%s\n",yytext); return RW_FOR;}
goto    {printf("%s\n",yytext); return RW_GOTO;}
if      {printf("%s\n",yytext); return RW_IF;}
int     {printf("%s\n",yytext); return RW_INT;}
long    {printf("%s\n",yytext); return RW_LONG;}
return  {printf("%s\n",yytext); return RW_RETURN;}
short   {printf("%s\n",yytext); return RW_SHORT;}
signed  {printf("%s\n",yytext); return RW_SIGNED;}
unsigned {printf("%s\n",yytext); return RW_UNSIGNED;}
void    {printf("%s\n",yytext); return RW_VOID;}
while   {printf("%s\n",yytext); return RW_WHILE;}
{id}    {yylval = node_identifier(yytext); printf("%s\n",yytext); return ID;}
{zero}  {yylval = node_number(0, INT); return INT;}
{error_zero} {fprintf(stderr,"Line %d: octal numbers not allowed (%s)\n",yylineno, yytext);};
{number} {int numberType = eval_num_type();
          if (!myerrno) { 
              yylval = node_number(eval_num(), numberType);  
              return numberType; 
          }
          myerrno = 0;
         }
\"      { tmpstr = (char *)malloc_string_no_content() ; 
          if (tmpstr != NULL) {
            BEGIN(IN_STRING);
            myerrno = 0;
          }
        }
<IN_STRING>{
\"      { /*This code builds up strings from their constituent pieces of escaped and non-escaped characters
          Case 3 is escaped characters, case 2 is escaped octal-digit characters, and case 1 is everything else.
          */
          BEGIN(INITIAL) ;
          #ifdef DEBUG
          int i;
          for (i=0; i< tmpstrlen; i++) {
              printf("--%c %d\n",tmpstr[i], tmpstrlen);
          }
          #endif
          yylval = node_string(tmpstr, tmpstrlen); 
          if (!myerrno) { return STRING; } else { myerrno = 0; }
        };
{char_case3}    { char eval_char = (char)eval_char_case_3();
                  char tmp[2];
                  tmp[0] = eval_char;
                  tmp[1] = 0; 
                  tmpstr = mystrcat(tmpstr, tmp, tmpstrlen, 1);
                  tmpstrlen += 1;  
                }
{char_case2}    { char eval_char = (char)eval_char_case_2();
                  char tmp[2];
                  tmp[0] = eval_char;
                  tmp[1] = 0; 
                  tmpstr = mystrcat(tmpstr, tmp, tmpstrlen, 1);
                  tmpstrlen += 1;
                }
\\              {
                    fprintf(stderr,"Line %d: bad escape sequence inside string (%s)\n", yylineno, yytext);
                    myerrno = 1;
                }
{char_case1}+   { tmpstr = mystrcat(tmpstr, (char *)malloc_string(), tmpstrlen,strlen(yytext));
                  tmpstrlen += strlen(yytext);
                }
\n              { myerrno = 1;
                  /*yylineno exceeds length of file when using EOF*/
                  fprintf(stderr,"Line %d: not allowed to have multi-line strings\n", yylineno-1);
                  BEGIN(INITIAL);
                } 
}
\'\'    { fprintf(stderr,"Line %d: empty character not allowed (%s)\n", yylineno, yytext); } 
\'      { BEGIN(IN_CHAR); myerrno = 0;}
<IN_CHAR>{
\'           { BEGIN(INITIAL); }
{char_case1} {yylval = node_number(eval_char_case_1(), CHAR) ; if (!myerrno) { return CHAR; }}
{char_case2} {yylval = node_number(eval_char_case_2(), CHAR) ; if (!myerrno) { return CHAR; }}
{char_case3} {yylval = node_number(eval_char_case_3(), CHAR) ; if (!myerrno) { return CHAR; }}
[^']+      {fprintf(stderr,"Line %d: Bad sequence inside character (%s)\n",yylineno,yytext);}
}
"("     {printf("%s\n",yytext); return PARENLEFT;}
")"     {printf("%s\n",yytext); return PARENRIGHT;}
"["     {printf("%s\n",yytext); return BRACKETLEFT;}
"]"     {printf("%s\n",yytext); return BRACKETRIGHT;}
"{"     {printf("%s\n",yytext); return BRACELEFT;}
"}"     {printf("%s\n",yytext); return BRACERIGHT;}
";"     {printf("%s\n",yytext); return SEPSEMICOLON;}
"++"    {printf("%s\n",yytext); return INCDECINCREMENT;}
"--"    {printf("%s\n",yytext); return INCDECDECREMENT;}
"!"     {printf("%s\n",yytext); return LOGNEG;}
"~"     {printf("%s\n",yytext); return BITWISENEG;}
"*"     {printf("%s\n",yytext); return MULTIPLY;}
"/"     {printf("%s\n",yytext); return DIVIDE;}
"%"     {printf("%s\n",yytext); return REMAINDER;}
"+"     {printf("%s\n",yytext); return ADDITION;}
"-"     {printf("%s\n",yytext); return SUBTRACTION;}
"<<"    {printf("%s\n",yytext); return LEFT;}
">>"    {printf("%s\n",yytext); return RIGHT;}
"<"     {printf("%s\n",yytext); return LT;}
"<="    {printf("%s\n",yytext); return LE;}
">"     {printf("%s\n",yytext); return GT;}
">="    {printf("%s\n",yytext); return GE;}
"=="    {printf("%s\n",yytext); return EQ;}
"!="    {printf("%s\n",yytext); return NE;}
"|"     {printf("%s\n",yytext); return BITOR;}
"^"     {printf("%s\n",yytext); return BITXOR;}
"&"     {printf("%s\n",yytext); return BITAND;}
"||"    {printf("%s\n",yytext); return LOGICALOR;}
"&&"    {printf("%s\n",yytext); return LOGICALAND;}
"?"     {printf("%s\n",yytext); return CONDQUEST;}
":"     {printf("%s\n",yytext); return CONDCOLON;}
"="     {printf("%s\n",yytext); return ASSIGNMENTSIMPLE;}
"+="    {printf("%s\n",yytext); return ASSIGNMENTADDITION;}
"-="    {printf("%s\n",yytext); return ASSIGNMENTSUBTRACTION;}
"*="    {printf("%s\n",yytext); return ASSIGNMENTMULTIPLICATION;}
"/="    {printf("%s\n",yytext); return ASSIGNMENTDIVISION;}
"%="    {printf("%s\n",yytext); return ASSIGNMENTREMAINDER;}
"<<="   {printf("%s\n",yytext); return ASSIGNMENTLEFTSHIFT;}
">>="   {printf("%s\n",yytext); return ASSIGNMENTRIGHTSHIFT;}
"&="    {printf("%s\n",yytext); return ASSIGNMENTBITWISEAND;}
"^="    {printf("%s\n",yytext); return ASSIGNMENTBITWISEXOR;}
"|="    {printf("%s\n",yytext); return ASSIGNMENTBITWISEOR;}
","     {return SEQUENTIALCOMMA;}

\*\/    { fprintf(stderr,"Line %d: malformed comment (%s)\n", yylineno, yytext); }
{any_char} {fprintf(stderr,"Line %d: unrecognized character (%s)\n", yylineno, yytext)
;}

%%

#include <stdlib.h>
#include <string.h>

/**
    malloc_id - create a string to represent an identifier

    parameter: none

    return: a string containing the name of the identifier

    side effects: prints error when out of memory, memory allocation on heap
    
**/

void *malloc_id(void) {
  char *id;

#ifdef DEBUG
  printf("malloc_id: Found id \"%s\" with length %d\n", yytext, yyleng);
  printf("malloc_id: yytext == %s\n", yytext);
#endif
  id = malloc(yyleng+1);
  if(id == (char *)NULL) {
    fprintf(stderr,"***Out of memory***\n");
  } else {
    strcpy(id, yytext);
  }
#ifdef DEBUG
  printf("malloc_id: returning %s -> \"%s\"\n", id, id);
#endif
  return id;
}

/**
    eval_num - determine the value of an integer constant

    parameter: none

    return: the numeric value of the number constant

    side effects: prints error when number is out of maximum range
    
**/
long int eval_num(void) {
  long int val;

#ifdef DEBUG
  printf("eval_num: Found number \"%s\" with length %d\n", yytext, yyleng);
#endif
  myerrno = 0;
  val = strtoul(yytext, 0, 10);
  if (errno != 0) {
    fprintf(stderr,"Line %d: integer constant exceeds maximum size of 4294967295 (%s)\n", yylineno, yytext);
    myerrno = 1;
    return 1;
  }
  return val;
}

/**
    eval_num_type - determine the type (short, int, unsigned long) of a number constant

    parameter: none

    return: the token type (enum yytokentype)

    side effects: prints error when number is out of maximum range
    
**/
enum yytokentype eval_num_type(void) {
    unsigned long longVal;
#ifdef DEBUG
  printf("eval_num_type: Found number \"%s\" with length %d\n", yytext, yyleng);
#endif
    myerrno = 0;
    longVal = strtoul(yytext, 0, 10);
    if (errno != 0) {
        fprintf(stderr,"Line %d: integer constant exceeds maximum size of 4294967295 (%s)\n", yylineno, yytext);
        myerrno = 1;
        return INT;
    }
    
    if (longVal <= 2147483647) {
        return INT;
    } else if (longVal <= 4294967295 ){
        return ULONG;
    } else {
        fprintf(stderr,"Line %d: integer constant exceeds maximum size of 4294967295 (%s)\n", yylineno, yytext);
        myerrno = 1;
        return ULONG;
    }

}

/**
    eval_char_case_1 - determine the character value of a non-escaped character constant

    parameter: none

    return: the character value of the character constant

    side effects: none
    
**/
char eval_char_case_1(void) {
  char val;

#ifdef DEBUG
  printf("eval_char: Found character (case 1) \"%s\"\n", yytext);
#endif

  val = yytext[0];
  return val;
}

/**
    eval_char_case_2 - determine the character value of an octal character constant

    parameter: none

    return: the character value of the octal character constant

    side effects: prints error when octal digits are out of range
    
**/
char eval_char_case_2(void) {
  int val;

#ifdef DEBUG
  printf("eval_char: Found character (case 2) \"%s\"\n", yytext);
#endif
  
  val = (int)strtoul(&yytext[1],0,8);
  myerrno = 0;
  if (val < 0 || val > 255) {
    fprintf(stderr,"Line %d: char value exceeds 1 byte (%s)\n", yylineno, yytext);
    myerrno = 1;
    return 1;
  }

  return (char)val;
}

/**
    eval_char_case_3 - determine the character value of an escaped character constant

    parameter: none

    return: the character value of the escaped character constant

    side effects: prints error when character isn't recognized
    
**/
char eval_char_case_3(void) {
  char val;

#ifdef DEBUG
  fprintf(stderr,"eval_char: Found character (case 3) \"%s\"\n", yytext);
#endif
  myerrno = 0;
  switch(yytext[1]) {
    case 'n':
        val = '\12';
    break; 
    case 't':
        val = '\11';
    break;
    case 'b':
        val = '\10';
    break; 
    case 'r':
        val = '\15';
    break;
    case 'f':
        val = '\14';
    break; 
    case 'v':
        val = '\13';
    break;
    case '\\':
        val = '\134';
    break;
    case '\'':
        val = '\47';
    break;
    case '\"':
        val = '\42';
    break;
    case 'a':
        val = '\7';
    break; 
    case '?':
        val = '\77';
    break;  
    default:
        myerrno = 1;
        fprintf(stderr,"Line %d: Unrecognized escape character (%s)\n", yylineno, yytext);
    break;
  }
  
  return val;
}

/**
    malloc_string_no_content - allocate a new string to be built up from (potentially) many different escaped and non-scaped characters

    parameter: none

    return: a pointer to the new string 

    side effects: prints error when out of memory, memory allocated to the heap
    
**/

/*Allocate space for string, may be longer than actual string due to escape/octal characters.
Then we'll build up the actual string later by interpreting escapes.
*/
void *malloc_string_no_content(void){
    char *str;

#ifdef DEBUG
  printf("malloc_string: Found string \"%s\" with length %d\n", yytext,
	 yyleng);
  printf("malloc_string: yytext == %s\n", yytext);
#endif
  str = malloc(yyleng+1);
  if(str == (char *)NULL) {
    fprintf(stderr,"***Out of memory***\n");
    myerrno = 1;
    return NULL;
  } else {
    strcpy(str, "");
  }
#ifdef DEBUG
  printf("malloc_str: returning %s -> \"%s\"\n", str, str);
#endif
  
  tmpstrlen = 0;
  
  return str;
}

/**
    malloc_string - allocate a new string

    parameter: none

    return: a pointer to the new string

    side effects: prints error when out of memory, memory allocated to the heap
    
**/
void *malloc_string(void) {
  char *str;

#ifdef DEBUG
  printf("malloc_string: Found string \"%s\" with length %d\n", yytext,
	 yyleng);
  printf("malloc_string: yytext == %s\n", yytext);
#endif
  str = malloc(yyleng+1);
  if(str == (char *)NULL) {
    fprintf(stderr,"***Out of memory***\n");
    myerrno = 1;
    return NULL;
  } else {
    strcpy(str, yytext);
  }
#ifdef DEBUG
  printf("malloc_str: returning %s -> \"%s\"\n", str, str);
#endif
  return str;
}

/**
    mystrcat - Function for concatenating strings with null characters in the middle of them.
    Based on example from http://linux.die.net/man/3/strcat

    parameter: destination string, source string, destination length, source length

    return: a string containing the two arguments concatenated

    side effects: char * dest parameter gets modified

**/
char * mystrcat (char * dest, char * src, int destLength, int srcLength) {
int i;
      
for (i = 0 ; i < srcLength; i++)
  dest[destLength + i] = src[i];
dest[destLength + i] = '\0';
                         
return dest;  
}
