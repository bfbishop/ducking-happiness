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

#ifndef YY_YY_PARSER_H_INCLUDED
# define YY_YY_PARSER_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 1
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token type.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    IDENTIFIER = 258,
    NUMBER = 259,
    STRING = 260,
    BREAK = 261,
    CHAR = 262,
    CONTINUE = 263,
    DO = 264,
    ELSE = 265,
    FOR = 266,
    GOTO = 267,
    IF = 268,
    INT = 269,
    LONG = 270,
    RETURN = 271,
    SHORT = 272,
    SIGNED = 273,
    UNSIGNED = 274,
    VOID = 275,
    WHILE = 276,
    LEFT_PAREN = 277,
    RIGHT_PAREN = 278,
    LEFT_SQUARE = 279,
    RIGHT_SQUARE = 280,
    LEFT_CURLY = 281,
    RIGHT_CURLY = 282,
    AMPERSAND = 283,
    ASTERISK = 284,
    CARET = 285,
    COLON = 286,
    COMMA = 287,
    EQUAL = 288,
    EXCLAMATION = 289,
    GREATER = 290,
    LESS = 291,
    MINUS = 292,
    PERCENT = 293,
    PLUS = 294,
    SEMICOLON = 295,
    SLASH = 296,
    QUESTION = 297,
    TILDE = 298,
    VBAR = 299,
    AMPERSAND_AMPERSAND = 300,
    AMPERSAND_EQUAL = 301,
    ASTERISK_EQUAL = 302,
    CARET_EQUAL = 303,
    EQUAL_EQUAL = 304,
    EXCLAMATION_EQUAL = 305,
    GREATER_EQUAL = 306,
    GREATER_GREATER = 307,
    GREATER_GREATER_EQUAL = 308,
    LESS_EQUAL = 309,
    LESS_LESS = 310,
    LESS_LESS_EQUAL = 311,
    MINUS_EQUAL = 312,
    MINUS_MINUS = 313,
    PERCENT_EQUAL = 314,
    PLUS_EQUAL = 315,
    PLUS_PLUS = 316,
    SLASH_EQUAL = 317,
    VBAR_EQUAL = 318,
    VBAR_VBAR = 319
  };
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;

int yyparse (void);

#endif /* !YY_YY_PARSER_H_INCLUDED  */
