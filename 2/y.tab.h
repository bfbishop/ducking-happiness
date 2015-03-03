/* A Bison parser, made by GNU Bison 3.0.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2013 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

#ifndef YY_YY_Y_TAB_H_INCLUDED
# define YY_YY_Y_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token type.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    RW_BREAK = 258,
    RW_CHAR = 259,
    RW_CONTINUE = 260,
    RW_DO = 261,
    RW_ELSE = 262,
    RW_FOR = 263,
    RW_GOTO = 264,
    RW_IF = 265,
    RW_INT = 266,
    RW_LONG = 267,
    RW_RETURN = 268,
    RW_SHORT = 269,
    RW_SIGNED = 270,
    RW_UNSIGNED = 271,
    RW_VOID = 272,
    RW_WHILE = 273,
    PARENRIGHT = 274,
    ADDITION = 275,
    ASSIGNMENTADDITION = 276,
    ASSIGNMENTBITWISEAND = 277,
    ASSIGNMENTBITWISEOR = 278,
    ASSIGNMENTBITWISEXOR = 279,
    ASSIGNMENTDIVISION = 280,
    ASSIGNMENTLEFTSHIFT = 281,
    ASSIGNMENTMULTIPLICATION = 282,
    ASSIGNMENTREMAINDER = 283,
    ASSIGNMENTRIGHTSHIFT = 284,
    ASSIGNMENTSIMPLE = 285,
    ASSIGNMENTSUBTRACTION = 286,
    BITAND = 287,
    BITWISENEG = 288,
    BITOR = 289,
    BITXOR = 290,
    BRACELEFT = 291,
    BRACERIGHT = 292,
    BRACKETLEFT = 293,
    BRACKETRIGHT = 294,
    CHAR = 295,
    CONDCOLON = 296,
    CONDQUEST = 297,
    INT = 298,
    STRING = 299,
    DIVIDE = 300,
    EQ = 301,
    GE = 302,
    GT = 303,
    ID = 304,
    INCDECDECREMENT = 305,
    INCDECINCREMENT = 306,
    LE = 307,
    LEFT = 308,
    LOGICALAND = 309,
    LOGICALOR = 310,
    LOGNEG = 311,
    LT = 312,
    MULTIPLY = 313,
    NE = 314,
    PARENLEFT = 315,
    REMAINDER = 316,
    RIGHT = 317,
    SEPSEMICOLON = 318,
    SEQUENTIALCOMMA = 319,
    SUBTRACTION = 320,
    ULONG = 321
  };
#endif
/* Tokens.  */
#define RW_BREAK 258
#define RW_CHAR 259
#define RW_CONTINUE 260
#define RW_DO 261
#define RW_ELSE 262
#define RW_FOR 263
#define RW_GOTO 264
#define RW_IF 265
#define RW_INT 266
#define RW_LONG 267
#define RW_RETURN 268
#define RW_SHORT 269
#define RW_SIGNED 270
#define RW_UNSIGNED 271
#define RW_VOID 272
#define RW_WHILE 273
#define PARENRIGHT 274
#define ADDITION 275
#define ASSIGNMENTADDITION 276
#define ASSIGNMENTBITWISEAND 277
#define ASSIGNMENTBITWISEOR 278
#define ASSIGNMENTBITWISEXOR 279
#define ASSIGNMENTDIVISION 280
#define ASSIGNMENTLEFTSHIFT 281
#define ASSIGNMENTMULTIPLICATION 282
#define ASSIGNMENTREMAINDER 283
#define ASSIGNMENTRIGHTSHIFT 284
#define ASSIGNMENTSIMPLE 285
#define ASSIGNMENTSUBTRACTION 286
#define BITAND 287
#define BITWISENEG 288
#define BITOR 289
#define BITXOR 290
#define BRACELEFT 291
#define BRACERIGHT 292
#define BRACKETLEFT 293
#define BRACKETRIGHT 294
#define CHAR 295
#define CONDCOLON 296
#define CONDQUEST 297
#define INT 298
#define STRING 299
#define DIVIDE 300
#define EQ 301
#define GE 302
#define GT 303
#define ID 304
#define INCDECDECREMENT 305
#define INCDECINCREMENT 306
#define LE 307
#define LEFT 308
#define LOGICALAND 309
#define LOGICALOR 310
#define LOGNEG 311
#define LT 312
#define MULTIPLY 313
#define NE 314
#define PARENLEFT 315
#define REMAINDER 316
#define RIGHT 317
#define SEPSEMICOLON 318
#define SEQUENTIALCOMMA 319
#define SUBTRACTION 320
#define ULONG 321

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;

int yyparse (void);

#endif /* !YY_YY_Y_TAB_H_INCLUDED  */
