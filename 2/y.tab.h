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
    RW_FOR = 262,
    RW_GOTO = 263,
    RW_IF = 264,
    RW_INT = 265,
    RW_LONG = 266,
    RW_RETURN = 267,
    RW_SHORT = 268,
    RW_SIGNED = 269,
    RW_UNSIGNED = 270,
    RW_VOID = 271,
    RW_WHILE = 272,
    ADDITION = 273,
    ASSIGNMENTADDITION = 274,
    ASSIGNMENTBITWISEAND = 275,
    ASSIGNMENTBITWISEOR = 276,
    ASSIGNMENTBITWISEXOR = 277,
    ASSIGNMENTDIVISION = 278,
    ASSIGNMENTLEFTSHIFT = 279,
    ASSIGNMENTMULTIPLICATION = 280,
    ASSIGNMENTREMAINDER = 281,
    ASSIGNMENTRIGHTSHIFT = 282,
    ASSIGNMENTSIMPLE = 283,
    ASSIGNMENTSUBTRACTION = 284,
    BITAND = 285,
    BITWISENEG = 286,
    BITOR = 287,
    BITXOR = 288,
    BRACELEFT = 289,
    BRACERIGHT = 290,
    BRACKETLEFT = 291,
    BRACKETRIGHT = 292,
    CHAR = 293,
    CONDCOLON = 294,
    CONDQUEST = 295,
    INT = 296,
    STRING = 297,
    DIVIDE = 298,
    EQ = 299,
    GE = 300,
    GT = 301,
    ID = 302,
    INCDECDECREMENT = 303,
    INCDECINCREMENT = 304,
    LE = 305,
    LEFT = 306,
    LOGICALAND = 307,
    LOGICALOR = 308,
    LOGNEG = 309,
    LT = 310,
    MULTIPLY = 311,
    NE = 312,
    PARENLEFT = 313,
    PARENRIGHT = 314,
    RW_ELSE = 315,
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
#define RW_FOR 262
#define RW_GOTO 263
#define RW_IF 264
#define RW_INT 265
#define RW_LONG 266
#define RW_RETURN 267
#define RW_SHORT 268
#define RW_SIGNED 269
#define RW_UNSIGNED 270
#define RW_VOID 271
#define RW_WHILE 272
#define ADDITION 273
#define ASSIGNMENTADDITION 274
#define ASSIGNMENTBITWISEAND 275
#define ASSIGNMENTBITWISEOR 276
#define ASSIGNMENTBITWISEXOR 277
#define ASSIGNMENTDIVISION 278
#define ASSIGNMENTLEFTSHIFT 279
#define ASSIGNMENTMULTIPLICATION 280
#define ASSIGNMENTREMAINDER 281
#define ASSIGNMENTRIGHTSHIFT 282
#define ASSIGNMENTSIMPLE 283
#define ASSIGNMENTSUBTRACTION 284
#define BITAND 285
#define BITWISENEG 286
#define BITOR 287
#define BITXOR 288
#define BRACELEFT 289
#define BRACERIGHT 290
#define BRACKETLEFT 291
#define BRACKETRIGHT 292
#define CHAR 293
#define CONDCOLON 294
#define CONDQUEST 295
#define INT 296
#define STRING 297
#define DIVIDE 298
#define EQ 299
#define GE 300
#define GT 301
#define ID 302
#define INCDECDECREMENT 303
#define INCDECINCREMENT 304
#define LE 305
#define LEFT 306
#define LOGICALAND 307
#define LOGICALOR 308
#define LOGNEG 309
#define LT 310
#define MULTIPLY 311
#define NE 312
#define PARENLEFT 313
#define PARENRIGHT 314
#define RW_ELSE 315
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
