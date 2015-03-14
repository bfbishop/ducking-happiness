/* A Bison parser, made by GNU Bison 3.0.2.  */

/* Bison implementation for Yacc-like parsers in C

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

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output.  */
#define YYBISON 1

/* Bison version.  */
#define YYBISON_VERSION "3.0.2"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 0

/* Push parsers.  */
#define YYPUSH 0

/* Pull parsers.  */
#define YYPULL 1




/* Copy the first part of user declarations.  */
#line 11 "bparser.y" /* yacc.c:339  */


#define YYSTYPE struct node *
#define YYERROR_VERBOSE
#include <stdio.h>
#include "symbol.h"
#include "node.h"

int yylex(void);
int main(int argc, char * argv[]);
void yyerror(char const *s);
/*start of the parse tree*/
struct node * root_node;

extern FILE * yyin;

/*set to 1 in the event of a recoverable error*/
int recoverable_error = 0;


#line 87 "y.tab.c" /* yacc.c:339  */

# ifndef YY_NULLPTR
#  if defined __cplusplus && 201103L <= __cplusplus
#   define YY_NULLPTR nullptr
#  else
#   define YY_NULLPTR 0
#  endif
# endif

/* Enabling verbose error messages.  */
#ifdef YYERROR_VERBOSE
# undef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#else
# define YYERROR_VERBOSE 0
#endif

/* In a future release of Bison, this section will be replaced
   by #include "y.tab.h".  */
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

/* Copy the second part of user declarations.  */

#line 270 "y.tab.c" /* yacc.c:358  */

#ifdef short
# undef short
#endif

#ifdef YYTYPE_UINT8
typedef YYTYPE_UINT8 yytype_uint8;
#else
typedef unsigned char yytype_uint8;
#endif

#ifdef YYTYPE_INT8
typedef YYTYPE_INT8 yytype_int8;
#else
typedef signed char yytype_int8;
#endif

#ifdef YYTYPE_UINT16
typedef YYTYPE_UINT16 yytype_uint16;
#else
typedef unsigned short int yytype_uint16;
#endif

#ifdef YYTYPE_INT16
typedef YYTYPE_INT16 yytype_int16;
#else
typedef short int yytype_int16;
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif ! defined YYSIZE_T
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned int
# endif
#endif

#define YYSIZE_MAXIMUM ((YYSIZE_T) -1)

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(Msgid) dgettext ("bison-runtime", Msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(Msgid) Msgid
# endif
#endif

#ifndef YY_ATTRIBUTE
# if (defined __GNUC__                                               \
      && (2 < __GNUC__ || (__GNUC__ == 2 && 96 <= __GNUC_MINOR__)))  \
     || defined __SUNPRO_C && 0x5110 <= __SUNPRO_C
#  define YY_ATTRIBUTE(Spec) __attribute__(Spec)
# else
#  define YY_ATTRIBUTE(Spec) /* empty */
# endif
#endif

#ifndef YY_ATTRIBUTE_PURE
# define YY_ATTRIBUTE_PURE   YY_ATTRIBUTE ((__pure__))
#endif

#ifndef YY_ATTRIBUTE_UNUSED
# define YY_ATTRIBUTE_UNUSED YY_ATTRIBUTE ((__unused__))
#endif

#if !defined _Noreturn \
     && (!defined __STDC_VERSION__ || __STDC_VERSION__ < 201112)
# if defined _MSC_VER && 1200 <= _MSC_VER
#  define _Noreturn __declspec (noreturn)
# else
#  define _Noreturn YY_ATTRIBUTE ((__noreturn__))
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YYUSE(E) ((void) (E))
#else
# define YYUSE(E) /* empty */
#endif

#if defined __GNUC__ && 407 <= __GNUC__ * 100 + __GNUC_MINOR__
/* Suppress an incorrect diagnostic about yylval being uninitialized.  */
# define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN \
    _Pragma ("GCC diagnostic push") \
    _Pragma ("GCC diagnostic ignored \"-Wuninitialized\"")\
    _Pragma ("GCC diagnostic ignored \"-Wmaybe-uninitialized\"")
# define YY_IGNORE_MAYBE_UNINITIALIZED_END \
    _Pragma ("GCC diagnostic pop")
#else
# define YY_INITIAL_VALUE(Value) Value
#endif
#ifndef YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_END
#endif
#ifndef YY_INITIAL_VALUE
# define YY_INITIAL_VALUE(Value) /* Nothing. */
#endif


#if ! defined yyoverflow || YYERROR_VERBOSE

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined EXIT_SUCCESS
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
      /* Use EXIT_SUCCESS as a witness for stdlib.h.  */
#     ifndef EXIT_SUCCESS
#      define EXIT_SUCCESS 0
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's 'empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (0)
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined EXIT_SUCCESS \
       && ! ((defined YYMALLOC || defined malloc) \
             && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef EXIT_SUCCESS
#    define EXIT_SUCCESS 0
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined EXIT_SUCCESS
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined EXIT_SUCCESS
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* ! defined yyoverflow || YYERROR_VERBOSE */


#if (! defined yyoverflow \
     && (! defined __cplusplus \
         || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yytype_int16 yyss_alloc;
  YYSTYPE yyvs_alloc;
};

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (yytype_int16) + sizeof (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

# define YYCOPY_NEEDED 1

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack_alloc, Stack)                           \
    do                                                                  \
      {                                                                 \
        YYSIZE_T yynewbytes;                                            \
        YYCOPY (&yyptr->Stack_alloc, Stack, yysize);                    \
        Stack = &yyptr->Stack_alloc;                                    \
        yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
        yyptr += yynewbytes / sizeof (*yyptr);                          \
      }                                                                 \
    while (0)

#endif

#if defined YYCOPY_NEEDED && YYCOPY_NEEDED
/* Copy COUNT objects from SRC to DST.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(Dst, Src, Count) \
      __builtin_memcpy (Dst, Src, (Count) * sizeof (*(Src)))
#  else
#   define YYCOPY(Dst, Src, Count)              \
      do                                        \
        {                                       \
          YYSIZE_T yyi;                         \
          for (yyi = 0; yyi < (Count); yyi++)   \
            (Dst)[yyi] = (Src)[yyi];            \
        }                                       \
      while (0)
#  endif
# endif
#endif /* !YYCOPY_NEEDED */

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  34
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   1022

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  67
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  91
/* YYNRULES -- Number of rules.  */
#define YYNRULES  208
/* YYNSTATES -- Number of states.  */
#define YYNSTATES  310

/* YYTRANSLATE[YYX] -- Symbol number corresponding to YYX as returned
   by yylex, with out-of-bounds checking.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   321

#define YYTRANSLATE(YYX)                                                \
  ((unsigned int) (YYX) <= YYMAXUTOK ? yytranslate[YYX] : YYUNDEFTOK)

/* YYTRANSLATE[TOKEN-NUM] -- Symbol number corresponding to TOKEN-NUM
   as returned by yylex, without out-of-bounds checking.  */
static const yytype_uint8 yytranslate[] =
{
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50,    51,    52,    53,    54,
      55,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66
};

#if YYDEBUG
  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
static const yytype_uint16 yyrline[] =
{
       0,    91,    91,    93,    94,    95,    97,    98,   100,   101,
     103,   105,   106,   108,   109,   111,   112,   113,   114,   115,
     116,   117,   118,   119,   120,   121,   123,   124,   127,   129,
     130,   132,   133,   135,   137,   138,   140,   141,   142,   144,
     145,   147,   148,   150,   151,   153,   154,   156,   157,   158,
     159,   161,   163,   165,   167,   168,   169,   170,   172,   173,
     175,   177,   178,   180,   181,   182,   183,   184,   186,   187,
     188,   189,   191,   193,   194,   196,   197,   199,   201,   202,
     204,   206,   207,   208,   209,   210,   211,   212,   213,   215,
     220,   221,   224,   228,   232,   235,   237,   239,   242,   244,
     246,   248,   249,   251,   252,   253,   255,   256,   257,   259,
     261,   263,   264,   266,   268,   269,   271,   272,   274,   275,
     276,   278,   280,   282,   283,   284,   286,   287,   289,   291,
     294,   295,   297,   300,   302,   303,   304,   305,   306,   308,
     310,   312,   314,   315,   316,   318,   319,   321,   322,   323,
     324,   326,   327,   329,   330,   332,   333,   335,   336,   337,
     338,   339,   340,   341,   342,   343,   344,   345,   347,   349,
     350,   351,   352,   353,   354,   355,   356,   357,   358,   361,
     366,   367,   368,   369,   371,   372,   374,   375,   377,   378,
     380,   381,   382,   383,   384,   385,   386,   387,   388,   390,
     392,   395,   396,   397,   398,   399,   400,   402,   404
};
#endif

#if YYDEBUG || YYERROR_VERBOSE || 0
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "RW_BREAK", "RW_CHAR", "RW_CONTINUE",
  "RW_DO", "RW_ELSE", "RW_FOR", "RW_GOTO", "RW_IF", "RW_INT", "RW_LONG",
  "RW_RETURN", "RW_SHORT", "RW_SIGNED", "RW_UNSIGNED", "RW_VOID",
  "RW_WHILE", "PARENRIGHT", "ADDITION", "ASSIGNMENTADDITION",
  "ASSIGNMENTBITWISEAND", "ASSIGNMENTBITWISEOR", "ASSIGNMENTBITWISEXOR",
  "ASSIGNMENTDIVISION", "ASSIGNMENTLEFTSHIFT", "ASSIGNMENTMULTIPLICATION",
  "ASSIGNMENTREMAINDER", "ASSIGNMENTRIGHTSHIFT", "ASSIGNMENTSIMPLE",
  "ASSIGNMENTSUBTRACTION", "BITAND", "BITWISENEG", "BITOR", "BITXOR",
  "BRACELEFT", "BRACERIGHT", "BRACKETLEFT", "BRACKETRIGHT", "CHAR",
  "CONDCOLON", "CONDQUEST", "INT", "STRING", "DIVIDE", "EQ", "GE", "GT",
  "ID", "INCDECDECREMENT", "INCDECINCREMENT", "LE", "LEFT", "LOGICALAND",
  "LOGICALOR", "LOGNEG", "LT", "MULTIPLY", "NE", "PARENLEFT", "REMAINDER",
  "RIGHT", "SEPSEMICOLON", "SEQUENTIALCOMMA", "SUBTRACTION", "ULONG",
  "$accept", "program", "abstract_declarator", "additive_expr", "add_op",
  "address_expr", "array_declarator", "assignment_expr", "assignment_op",
  "bitwise_and_expr", "bitwise_negation_expr", "bitwise_or_expr",
  "bitwise_xor_expr", "break_statement", "cast_expr",
  "character_type_specifier", "comma_expr", "compound_statement",
  "conditional_expr", "conditional_statement", "constant", "constant_expr",
  "continue_statement", "decl", "declaration_or_statement",
  "declaration_or_statement_list", "declaration_specifiers", "declarator",
  "direct_abstract_declarator", "direct_declarator", "do_statement",
  "equality_expr", "equality_op", "expr", "expression_list",
  "expression_statement", "for_expr", "for_statement", "function_call",
  "function_declarator", "function_definition", "function_def_specifier",
  "goto_statement", "if_else_statement", "if_statement",
  "indirection_expr", "initial_clause", "initialized_declarator",
  "initialized_declarator_list", "integer_type_specifier",
  "iterative_statement", "label", "labeled_statement", "logical_and_expr",
  "logical_negation_expr", "logical_or_expr", "multiplicative_expr",
  "mult_op", "named_label", "null_statement", "parameter_decl",
  "parameter_list", "parameter_type_list", "parenthesized_expr", "pointer",
  "pointer_declarator", "postdecrement_expr", "postfix_expr",
  "postincrement_expr", "predecrement_expr", "preincrement_expr",
  "primary_expr", "relational_expr", "relational_op", "return_statement",
  "shift_expr", "shift_op", "signed_type_specifier", "simple_declarator",
  "statement", "subscript_expr", "top_level_decl", "translation_unit",
  "type_name", "type_specifier", "unary_expr", "unary_minus_expr",
  "unary_plus_expr", "unsigned_type_specifier", "void_type_specifier",
  "while_statement", YY_NULLPTR
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[NUM] -- (External) token number corresponding to the
   (internal) symbol number NUM (which must be that of a token).  */
static const yytype_uint16 yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278,   279,   280,   281,   282,   283,   284,
     285,   286,   287,   288,   289,   290,   291,   292,   293,   294,
     295,   296,   297,   298,   299,   300,   301,   302,   303,   304,
     305,   306,   307,   308,   309,   310,   311,   312,   313,   314,
     315,   316,   317,   318,   319,   320,   321
};
# endif

#define YYPACT_NINF -215

#define yypact_value_is_default(Yystate) \
  (!!((Yystate) == (-215)))

#define YYTABLE_NINF -122

#define yytable_value_is_error(Yytable_value) \
  0

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
static const yytype_int16 yypact[] =
{
     142,   158,  -215,  -215,    18,    35,   172,   181,  -215,    21,
    -215,  -215,    20,  -215,     1,  -215,  -215,  -215,   108,  -215,
    -215,  -215,  -215,  -215,  -215,  -215,  -215,  -215,    39,    72,
    -215,  -215,    76,    78,  -215,  -215,    45,    20,  -215,    68,
     -16,  -215,  -215,    30,    13,  -215,  -215,   239,  -215,  -215,
    -215,  -215,  -215,  -215,  -215,    87,   700,   158,  -215,    20,
     -16,   369,    52,    53,   433,    57,    79,    58,    16,    61,
     910,   910,   910,  -215,  -215,  -215,  -215,    90,   945,   945,
     910,   910,   490,  -215,   910,  -215,   -12,  -215,  -215,   105,
    -215,   114,   115,  -215,  -215,    88,  -215,  -215,  -215,  -215,
    -215,  -215,  -215,   305,    20,  -215,   -28,    98,  -215,  -215,
    -215,  -215,  -215,  -215,  -215,  -215,   123,  -215,   101,  -215,
     -25,    69,  -215,  -215,  -215,  -215,    50,  -215,  -215,  -215,
    -215,    92,  -215,    15,  -215,  -215,   991,  -215,  -215,  -215,
    -215,  -215,  -215,  -215,   126,  -215,    26,  -215,   103,   159,
    -215,  -215,  -215,  -215,  -215,  -215,   161,   735,   433,  -215,
     117,   910,  -215,   118,   910,  -215,  -215,  -215,   910,  -215,
    -215,  -215,  -215,    32,   163,   169,  -215,  -215,  -215,   910,
     910,   910,   910,   910,  -215,  -215,  -215,  -215,   910,  -215,
     433,   910,   910,   910,  -215,  -215,  -215,   910,   910,  -215,
    -215,   525,  -215,  -215,  -215,  -215,   910,  -215,  -215,   910,
    -215,  -215,  -215,  -215,  -215,  -215,  -215,  -215,  -215,  -215,
    -215,   910,  -215,   770,    26,  -215,  -215,   153,    47,   158,
    -215,   134,   805,  -215,   133,  -215,  -215,   178,  -215,   180,
      32,  -215,   -15,  -215,   910,    69,   -28,   115,   105,  -215,
      92,  -215,   114,   160,   101,  -215,   168,  -215,  -215,    -6,
      15,   -12,  -215,  -215,   170,   183,   840,   153,  -215,   910,
     560,   137,   875,   433,   433,  -215,   910,  -215,  -215,   910,
    -215,  -215,  -215,   171,   189,  -215,   192,   595,   630,   154,
     209,  -215,  -215,  -215,  -215,   155,  -215,  -215,   200,  -215,
     204,   665,   433,  -215,  -215,  -215,  -215,   206,  -215,  -215
};

  /* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
     Performed when YYTABLE does not specify something else to do.  Zero
     means the default is an error.  */
static const yytype_uint8 yydefact[] =
{
       0,     0,    36,   161,   164,   157,   163,   204,   207,     0,
     105,   180,     0,   181,     0,   188,   103,   184,     0,    60,
     104,   189,   182,   183,   165,   158,    37,   162,   166,   159,
      38,   203,   206,   202,     1,   168,   130,     0,    71,   100,
      62,    70,   101,     0,     0,    61,    68,     0,    93,   185,
     167,   160,   205,   201,   131,     0,     0,     0,    53,     0,
     132,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,    41,    49,    47,    50,   142,     0,     0,
       0,     0,     0,   122,     0,    48,   153,   195,    39,    31,
     194,   111,    29,   174,   116,    77,   171,    13,   172,   143,
     175,    54,    58,     0,     0,   107,    26,     0,   169,   108,
     136,   177,    46,    45,   196,   173,     0,   170,   114,   193,
      43,     6,   109,   178,   144,   138,   190,   137,   198,   197,
     134,    73,   176,   145,    55,   135,    34,   191,   192,   106,
      69,    11,   142,    51,     0,    34,   124,   126,   128,     0,
     100,   102,    56,    57,    33,    52,     0,     0,     0,   121,
       0,     0,   151,     0,     0,   200,    10,    28,     0,   140,
     141,   113,    98,   186,     0,     0,   199,     8,     9,     0,
       0,     0,     0,     0,    42,    59,    75,    76,     0,    80,
       0,     0,     0,     0,   119,   118,   120,     0,     0,   133,
     139,     0,   150,   149,   148,   147,     0,   155,   156,     0,
      16,    23,    25,    24,    19,    21,    18,    20,    22,    15,
      17,     0,    12,     0,     0,   125,   123,     4,     3,     0,
      92,     0,     0,    99,     0,    89,    95,     0,   152,     0,
       0,   187,     3,   129,     0,     7,    27,    30,    32,    40,
      74,   110,   112,     0,   115,   117,     0,    90,    78,     0,
     146,   154,    14,    64,     0,     0,     0,     5,   127,     0,
       0,     0,     0,     0,     0,    35,     0,   179,    91,     0,
      65,    63,    66,     0,     0,    85,     0,     0,     0,     0,
      97,   208,    44,    79,    67,     0,    83,    84,     0,    88,
       0,     0,     0,    72,    82,    86,    87,     0,    96,    81
};

  /* YYPGOTO[NTERM-NUM].  */
static const yytype_int16 yypgoto[] =
{
    -215,  -215,  -134,    17,  -215,  -215,  -215,  -174,  -215,    46,
    -215,    36,    48,  -215,   -29,  -215,  -215,   217,   -52,  -215,
    -215,  -203,  -215,    10,   129,  -215,   -42,    -5,  -214,   -38,
    -215,    54,  -215,   -66,  -215,  -215,  -215,  -215,  -215,  -215,
     232,  -215,  -215,  -215,  -215,  -215,  -215,   177,  -215,  -215,
    -215,  -215,  -215,    44,  -215,  -215,    59,  -215,   173,  -215,
      29,  -215,  -215,  -215,   -35,  -215,  -215,  -215,  -215,  -215,
    -215,  -215,    73,  -215,  -215,    40,  -215,  -215,  -215,   -61,
    -215,   242,  -215,  -215,  -215,   -46,  -215,  -215,  -215,  -215,
    -215
};

  /* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,     9,   265,    86,   179,    87,    38,    88,   221,    89,
      90,    91,    92,    93,    94,    10,    95,    96,    97,    98,
      99,   144,   100,    11,   102,   103,    12,    55,   227,    40,
     105,   106,   188,   107,   259,   108,   158,   109,   110,    41,
      13,    14,   111,   112,   113,   114,   234,    42,    43,    15,
     115,   116,   117,   118,   119,   120,   121,   197,   122,   123,
     147,   148,   149,   124,    44,    45,   125,   126,   127,   128,
     129,   130,   131,   206,   132,   133,   209,    16,    46,   134,
     135,    17,    18,   175,    19,   136,   137,   138,    20,    21,
     139
};

  /* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule whose
     number is the opposite.  If YYTABLE_NINF, syntax error.  */
static const yytype_int16 yytable[] =
{
     153,    54,   163,   156,   143,   104,    60,    39,   177,   249,
     145,    22,   225,   278,   267,   146,   174,   192,   186,   104,
     264,    34,    56,   223,   145,   145,   145,   258,   267,    24,
     193,   187,   169,   170,   145,   145,    70,    47,   145,   241,
     173,   165,   166,   167,    57,   240,    25,   262,    71,    72,
      50,   171,   172,   178,   150,   176,    74,   101,   279,    75,
      76,   104,    35,   283,   223,   142,    78,    79,   207,    35,
     223,   152,    80,    37,    81,    35,    82,   208,    36,   162,
      37,    84,    85,    51,    36,   223,   224,    52,   198,    53,
      36,   233,   240,    58,    59,   237,    35,   235,   239,   150,
     199,   200,   174,    36,   -94,   293,   140,   224,    -2,     1,
     201,   228,     2,   101,   194,   154,   155,   157,   161,     3,
       4,   164,     5,     6,     7,     8,   253,   195,   159,   251,
     196,  -121,   256,   145,   145,   145,   145,   180,   242,   202,
     203,   226,   145,     1,   204,   145,     2,   145,   181,   205,
     182,   145,   183,     3,     4,   191,     5,     6,     7,     8,
     145,   189,     2,   145,   190,   222,   271,   229,   255,     3,
       4,   143,     5,     6,     7,     8,    26,   145,   230,   231,
     236,   238,   243,    27,    28,    30,    29,   146,   244,   228,
      60,   266,    31,    32,   269,    33,   272,   273,   145,   274,
     287,   276,   281,   284,   286,   242,   289,   277,   295,   280,
     294,   296,   290,   291,   143,   275,   302,   301,   303,   304,
     145,   298,   300,   305,   292,   309,   261,   252,   248,   247,
     145,    48,   185,    23,   246,   307,   151,   254,   245,   160,
      61,   308,    62,     2,    63,    64,   260,    65,    66,    67,
       3,     4,    68,     5,     6,     7,     8,    69,   268,    70,
      49,   250,     0,     0,     0,     0,     0,     0,     0,     0,
       0,    71,    72,     0,     0,    47,    73,     0,     0,    74,
       0,     0,    75,    76,     0,     0,     0,     0,    77,    78,
      79,     0,     0,     0,     0,    80,     0,    81,     0,    82,
       0,     0,    83,     0,    84,    85,    61,     0,    62,     2,
      63,    64,     0,    65,    66,    67,     3,     4,    68,     5,
       6,     7,     8,    69,     0,    70,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,    71,    72,     0,
       0,    47,   184,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,    77,    78,    79,     0,     0,     0,
       0,    80,     0,    81,     0,    82,     0,     0,    83,     0,
      84,    85,    62,     2,    63,    64,     0,    65,    66,    67,
       3,     4,    68,     5,     6,     7,     8,    69,     0,    70,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,    71,    72,     0,     0,    47,     0,     0,     0,    74,
       0,     0,    75,    76,     0,     0,     0,     0,    77,    78,
      79,     0,     0,     0,     0,    80,     0,    81,     0,    82,
       0,     0,    83,     0,    84,    85,    62,     0,    63,    64,
       0,    65,    66,    67,     0,     0,    68,     0,     0,     0,
       0,    69,     0,    70,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,    71,    72,     0,     0,    47,
       0,     0,     0,    74,     0,     0,    75,    76,     0,     0,
       0,     0,    77,    78,    79,     0,     0,     0,     0,    80,
       0,    81,     0,    82,     2,     0,    83,     0,    84,    85,
       0,     3,     4,     0,     5,     6,     7,     8,     0,     0,
      70,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,    71,    72,     0,     0,     0,     0,     0,     0,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,   257,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,   285,
      70,    80,     0,    81,     0,    82,     0,     0,     0,     0,
      84,    85,    71,    72,     0,     0,     0,     0,     0,     0,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,   297,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,   299,
      70,    80,     0,    81,     0,    82,     0,     0,     0,     0,
      84,    85,    71,    72,     0,     0,     0,     0,     0,     0,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,   306,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,     0,
      70,    80,     0,    81,     0,    82,     0,     0,     0,     0,
      84,    85,    71,    72,     0,     0,     0,     0,     0,   141,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,     0,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,     0,
      70,    80,     0,    81,     0,    82,     0,     0,   232,     0,
      84,    85,    71,    72,     0,     0,     0,     0,     0,   263,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,     0,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,     0,
      70,    80,     0,    81,     0,    82,     0,     0,   270,     0,
      84,    85,    71,    72,     0,     0,     0,     0,     0,   282,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,     0,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,     0,
      70,    80,     0,    81,     0,    82,     0,     0,   288,     0,
      84,    85,    71,    72,     0,     0,     0,     0,     0,     0,
      74,     0,     0,    75,    76,     0,     0,     0,     0,   142,
      78,    79,     0,     0,     0,    70,    80,     0,    81,     0,
      82,     0,     0,     0,     0,    84,    85,    71,    72,     0,
       0,     0,     0,     0,     0,    74,     0,     0,    75,    76,
       0,     0,     0,     0,   142,    78,    79,     0,     0,     0,
       0,    80,     0,    81,     0,   168,     0,     0,     0,     0,
      84,    85,   210,   211,   212,   213,   214,   215,   216,   217,
     218,   219,   220
};

static const yytype_int16 yycheck[] =
{
      61,    36,    68,    64,    56,    47,    44,    12,    20,   183,
      56,     1,   146,    19,   228,    57,    82,    42,    46,    61,
     223,     0,    38,    38,    70,    71,    72,   201,   242,    11,
      55,    59,    78,    79,    80,    81,    20,    36,    84,   173,
      82,    70,    71,    72,    60,    60,    11,   221,    32,    33,
      11,    80,    81,    65,    59,    84,    40,    47,    64,    43,
      44,   103,    49,   266,    38,    49,    50,    51,    53,    49,
      38,    61,    56,    60,    58,    49,    60,    62,    58,    63,
      60,    65,    66,    11,    58,    38,    60,    11,    38,    11,
      58,   157,    60,    63,    64,   161,    49,   158,   164,   104,
      50,    51,   168,    58,    36,   279,    19,    60,     0,     1,
      60,   146,     4,   103,    45,    63,    63,    60,    60,    11,
      12,    60,    14,    15,    16,    17,   192,    58,    49,   190,
      61,    41,   198,   179,   180,   181,   182,    32,   173,    47,
      48,   146,   188,     1,    52,   191,     4,   193,    34,    57,
      35,   197,    64,    11,    12,    54,    14,    15,    16,    17,
     206,    63,     4,   209,    41,    39,   232,    64,   197,    11,
      12,   223,    14,    15,    16,    17,     4,   223,    19,    18,
      63,    63,    19,    11,    12,     4,    14,   229,    19,   224,
     228,    38,    11,    12,    60,    14,    63,    19,   244,    19,
      63,    41,    19,   269,   270,   240,   272,    39,    19,    39,
      39,    19,   273,   274,   266,   244,     7,    63,    63,    19,
     266,   287,   288,    19,   276,    19,   209,   191,   182,   181,
     276,    14,   103,     1,   180,   301,    59,   193,   179,    66,
       1,   302,     3,     4,     5,     6,   206,     8,     9,    10,
      11,    12,    13,    14,    15,    16,    17,    18,   229,    20,
      18,   188,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    32,    33,    -1,    -1,    36,    37,    -1,    -1,    40,
      -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,    50,
      51,    -1,    -1,    -1,    -1,    56,    -1,    58,    -1,    60,
      -1,    -1,    63,    -1,    65,    66,     1,    -1,     3,     4,
       5,     6,    -1,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    -1,    20,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    32,    33,    -1,
      -1,    36,    37,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      -1,    56,    -1,    58,    -1,    60,    -1,    -1,    63,    -1,
      65,    66,     3,     4,     5,     6,    -1,     8,     9,    10,
      11,    12,    13,    14,    15,    16,    17,    18,    -1,    20,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    32,    33,    -1,    -1,    36,    -1,    -1,    -1,    40,
      -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,    50,
      51,    -1,    -1,    -1,    -1,    56,    -1,    58,    -1,    60,
      -1,    -1,    63,    -1,    65,    66,     3,    -1,     5,     6,
      -1,     8,     9,    10,    -1,    -1,    13,    -1,    -1,    -1,
      -1,    18,    -1,    20,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    32,    33,    -1,    -1,    36,
      -1,    -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,
      -1,    -1,    49,    50,    51,    -1,    -1,    -1,    -1,    56,
      -1,    58,    -1,    60,     4,    -1,    63,    -1,    65,    66,
      -1,    11,    12,    -1,    14,    15,    16,    17,    -1,    -1,
      20,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    32,    33,    -1,    -1,    -1,    -1,    -1,    -1,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    19,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    19,
      20,    56,    -1,    58,    -1,    60,    -1,    -1,    -1,    -1,
      65,    66,    32,    33,    -1,    -1,    -1,    -1,    -1,    -1,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    19,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    19,
      20,    56,    -1,    58,    -1,    60,    -1,    -1,    -1,    -1,
      65,    66,    32,    33,    -1,    -1,    -1,    -1,    -1,    -1,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    19,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      20,    56,    -1,    58,    -1,    60,    -1,    -1,    -1,    -1,
      65,    66,    32,    33,    -1,    -1,    -1,    -1,    -1,    39,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    -1,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      20,    56,    -1,    58,    -1,    60,    -1,    -1,    63,    -1,
      65,    66,    32,    33,    -1,    -1,    -1,    -1,    -1,    39,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    -1,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      20,    56,    -1,    58,    -1,    60,    -1,    -1,    63,    -1,
      65,    66,    32,    33,    -1,    -1,    -1,    -1,    -1,    39,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    -1,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      20,    56,    -1,    58,    -1,    60,    -1,    -1,    63,    -1,
      65,    66,    32,    33,    -1,    -1,    -1,    -1,    -1,    -1,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    -1,    20,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    -1,    65,    66,    32,    33,    -1,
      -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,    43,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      -1,    56,    -1,    58,    -1,    60,    -1,    -1,    -1,    -1,
      65,    66,    21,    22,    23,    24,    25,    26,    27,    28,
      29,    30,    31
};

  /* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,     1,     4,    11,    12,    14,    15,    16,    17,    68,
      82,    90,    93,   107,   108,   116,   144,   148,   149,   151,
     155,   156,    90,   107,    11,    11,     4,    11,    12,    14,
       4,    11,    12,    14,     0,    49,    58,    60,    73,    94,
      96,   106,   114,   115,   131,   132,   145,    36,    84,   148,
      11,    11,    11,    11,   131,    94,    38,    60,    63,    64,
      96,     1,     3,     5,     6,     8,     9,    10,    13,    18,
      20,    32,    33,    37,    40,    43,    44,    49,    50,    51,
      56,    58,    60,    63,    65,    66,    70,    72,    74,    76,
      77,    78,    79,    80,    81,    83,    84,    85,    86,    87,
      89,    90,    91,    92,    93,    97,    98,   100,   102,   104,
     105,   109,   110,   111,   112,   117,   118,   119,   120,   121,
     122,   123,   125,   126,   130,   133,   134,   135,   136,   137,
     138,   139,   141,   142,   146,   147,   152,   153,   154,   157,
      19,    39,    49,    85,    88,   152,    93,   127,   128,   129,
      94,   114,    90,   146,    63,    63,   146,    60,   103,    49,
     125,    60,    63,   100,    60,    81,    81,    81,    60,   152,
     152,    81,    81,    93,   100,   150,    81,    20,    65,    71,
      32,    34,    35,    64,    37,    91,    46,    59,    99,    63,
      41,    54,    42,    55,    45,    58,    61,   124,    38,    50,
      51,    60,    47,    48,    52,    57,   140,    53,    62,   143,
      21,    22,    23,    24,    25,    26,    27,    28,    29,    30,
      31,    75,    39,    38,    60,    69,    94,    95,   131,    64,
      19,    18,    63,   100,   113,   146,    63,   100,    63,   100,
      60,    69,   131,    19,    19,   123,    98,    79,    76,    74,
     139,   146,    78,   100,   120,    81,   100,    19,    74,   101,
     142,    70,    74,    39,    88,    69,    38,    95,   127,    60,
      63,   100,    63,    19,    19,    81,    41,    39,    19,    64,
      39,    19,    39,    88,   100,    19,   100,    63,    63,   100,
     146,   146,    85,    74,    39,    19,    19,    19,   100,    19,
     100,    63,     7,    63,    19,    19,    19,   100,   146,    19
};

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,    67,    68,    69,    69,    69,    70,    70,    71,    71,
      72,    73,    73,    74,    74,    75,    75,    75,    75,    75,
      75,    75,    75,    75,    75,    75,    76,    76,    77,    78,
      78,    79,    79,    80,    81,    81,    82,    82,    82,    83,
      83,    84,    84,    85,    85,    86,    86,    87,    87,    87,
      87,    88,    89,    90,    91,    91,    91,    91,    92,    92,
      93,    94,    94,    95,    95,    95,    95,    95,    96,    96,
      96,    96,    97,    98,    98,    99,    99,   100,   101,   101,
     102,   103,   103,   103,   103,   103,   103,   103,   103,   104,
     105,   105,   106,   107,   108,   109,   110,   111,   112,   113,
     114,   115,   115,   116,   116,   116,   117,   117,   117,   118,
     119,   120,   120,   121,   122,   122,   123,   123,   124,   124,
     124,   125,   126,   127,   127,   127,   128,   128,   129,   130,
     131,   131,   132,   133,   134,   134,   134,   134,   134,   135,
     136,   137,   138,   138,   138,   139,   139,   140,   140,   140,
     140,   141,   141,   142,   142,   143,   143,   144,   144,   144,
     144,   144,   144,   144,   144,   144,   144,   144,   145,   146,
     146,   146,   146,   146,   146,   146,   146,   146,   146,   147,
     148,   148,   148,   148,   149,   149,   150,   150,   151,   151,
     152,   152,   152,   152,   152,   152,   152,   152,   152,   153,
     154,   155,   155,   155,   155,   155,   155,   156,   157
};

  /* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     1,     1,     1,     2,     1,     3,     1,     1,
       2,     3,     4,     1,     3,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     3,     2,     1,
       3,     1,     3,     2,     1,     4,     1,     2,     2,     1,
       3,     2,     3,     1,     5,     1,     1,     1,     1,     1,
       1,     1,     2,     3,     1,     1,     2,     2,     1,     2,
       1,     1,     1,     3,     2,     3,     3,     4,     1,     3,
       1,     1,     7,     1,     3,     1,     1,     1,     1,     3,
       2,     7,     6,     5,     5,     4,     6,     6,     5,     3,
       3,     4,     4,     2,     2,     3,     7,     5,     2,     1,
       1,     1,     3,     1,     1,     1,     1,     1,     1,     1,
       3,     1,     3,     2,     1,     3,     1,     3,     1,     1,
       1,     1,     1,     2,     1,     2,     1,     3,     1,     3,
       1,     2,     2,     2,     1,     1,     1,     1,     1,     2,
       2,     2,     1,     1,     1,     1,     3,     1,     1,     1,
       1,     2,     3,     1,     3,     1,     1,     1,     2,     2,
       3,     1,     2,     1,     1,     2,     2,     3,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     4,
       1,     1,     2,     2,     1,     2,     1,     2,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     1,     2,
       2,     3,     2,     2,     1,     3,     2,     1,     5
};


#define yyerrok         (yyerrstatus = 0)
#define yyclearin       (yychar = YYEMPTY)
#define YYEMPTY         (-2)
#define YYEOF           0

#define YYACCEPT        goto yyacceptlab
#define YYABORT         goto yyabortlab
#define YYERROR         goto yyerrorlab


#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)                                  \
do                                                              \
  if (yychar == YYEMPTY)                                        \
    {                                                           \
      yychar = (Token);                                         \
      yylval = (Value);                                         \
      YYPOPSTACK (yylen);                                       \
      yystate = *yyssp;                                         \
      goto yybackup;                                            \
    }                                                           \
  else                                                          \
    {                                                           \
      yyerror (YY_("syntax error: cannot back up")); \
      YYERROR;                                                  \
    }                                                           \
while (0)

/* Error token number */
#define YYTERROR        1
#define YYERRCODE       256



/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)                        \
do {                                            \
  if (yydebug)                                  \
    YYFPRINTF Args;                             \
} while (0)

/* This macro is provided for backward compatibility. */
#ifndef YY_LOCATION_PRINT
# define YY_LOCATION_PRINT(File, Loc) ((void) 0)
#endif


# define YY_SYMBOL_PRINT(Title, Type, Value, Location)                    \
do {                                                                      \
  if (yydebug)                                                            \
    {                                                                     \
      YYFPRINTF (stderr, "%s ", Title);                                   \
      yy_symbol_print (stderr,                                            \
                  Type, Value); \
      YYFPRINTF (stderr, "\n");                                           \
    }                                                                     \
} while (0)


/*----------------------------------------.
| Print this symbol's value on YYOUTPUT.  |
`----------------------------------------*/

static void
yy_symbol_value_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
{
  FILE *yyo = yyoutput;
  YYUSE (yyo);
  if (!yyvaluep)
    return;
# ifdef YYPRINT
  if (yytype < YYNTOKENS)
    YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# endif
  YYUSE (yytype);
}


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

static void
yy_symbol_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
{
  YYFPRINTF (yyoutput, "%s %s (",
             yytype < YYNTOKENS ? "token" : "nterm", yytname[yytype]);

  yy_symbol_value_print (yyoutput, yytype, yyvaluep);
  YYFPRINTF (yyoutput, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

static void
yy_stack_print (yytype_int16 *yybottom, yytype_int16 *yytop)
{
  YYFPRINTF (stderr, "Stack now");
  for (; yybottom <= yytop; yybottom++)
    {
      int yybot = *yybottom;
      YYFPRINTF (stderr, " %d", yybot);
    }
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)                            \
do {                                                            \
  if (yydebug)                                                  \
    yy_stack_print ((Bottom), (Top));                           \
} while (0)


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

static void
yy_reduce_print (yytype_int16 *yyssp, YYSTYPE *yyvsp, int yyrule)
{
  unsigned long int yylno = yyrline[yyrule];
  int yynrhs = yyr2[yyrule];
  int yyi;
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %lu):\n",
             yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      YYFPRINTF (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr,
                       yystos[yyssp[yyi + 1 - yynrhs]],
                       &(yyvsp[(yyi + 1) - (yynrhs)])
                                              );
      YYFPRINTF (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)          \
do {                                    \
  if (yydebug)                          \
    yy_reduce_print (yyssp, yyvsp, Rule); \
} while (0)

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args)
# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif


#if YYERROR_VERBOSE

# ifndef yystrlen
#  if defined __GLIBC__ && defined _STRING_H
#   define yystrlen strlen
#  else
/* Return the length of YYSTR.  */
static YYSIZE_T
yystrlen (const char *yystr)
{
  YYSIZE_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
#  endif
# endif

# ifndef yystpcpy
#  if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#   define yystpcpy stpcpy
#  else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
static char *
yystpcpy (char *yydest, const char *yysrc)
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
#  endif
# endif

# ifndef yytnamerr
/* Copy to YYRES the contents of YYSTR after stripping away unnecessary
   quotes and backslashes, so that it's suitable for yyerror.  The
   heuristic is that double-quoting is unnecessary unless the string
   contains an apostrophe, a comma, or backslash (other than
   backslash-backslash).  YYSTR is taken from yytname.  If YYRES is
   null, do not copy; instead, return the length of what the result
   would have been.  */
static YYSIZE_T
yytnamerr (char *yyres, const char *yystr)
{
  if (*yystr == '"')
    {
      YYSIZE_T yyn = 0;
      char const *yyp = yystr;

      for (;;)
        switch (*++yyp)
          {
          case '\'':
          case ',':
            goto do_not_strip_quotes;

          case '\\':
            if (*++yyp != '\\')
              goto do_not_strip_quotes;
            /* Fall through.  */
          default:
            if (yyres)
              yyres[yyn] = *yyp;
            yyn++;
            break;

          case '"':
            if (yyres)
              yyres[yyn] = '\0';
            return yyn;
          }
    do_not_strip_quotes: ;
    }

  if (! yyres)
    return yystrlen (yystr);

  return yystpcpy (yyres, yystr) - yyres;
}
# endif

/* Copy into *YYMSG, which is of size *YYMSG_ALLOC, an error message
   about the unexpected token YYTOKEN for the state stack whose top is
   YYSSP.

   Return 0 if *YYMSG was successfully written.  Return 1 if *YYMSG is
   not large enough to hold the message.  In that case, also set
   *YYMSG_ALLOC to the required number of bytes.  Return 2 if the
   required number of bytes is too large to store.  */
static int
yysyntax_error (YYSIZE_T *yymsg_alloc, char **yymsg,
                yytype_int16 *yyssp, int yytoken)
{
  YYSIZE_T yysize0 = yytnamerr (YY_NULLPTR, yytname[yytoken]);
  YYSIZE_T yysize = yysize0;
  enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
  /* Internationalized format string. */
  const char *yyformat = YY_NULLPTR;
  /* Arguments of yyformat. */
  char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];
  /* Number of reported tokens (one for the "unexpected", one per
     "expected"). */
  int yycount = 0;

  /* There are many possibilities here to consider:
     - If this state is a consistent state with a default action, then
       the only way this function was invoked is if the default action
       is an error action.  In that case, don't check for expected
       tokens because there are none.
     - The only way there can be no lookahead present (in yychar) is if
       this state is a consistent state with a default action.  Thus,
       detecting the absence of a lookahead is sufficient to determine
       that there is no unexpected or expected token to report.  In that
       case, just report a simple "syntax error".
     - Don't assume there isn't a lookahead just because this state is a
       consistent state with a default action.  There might have been a
       previous inconsistent state, consistent state with a non-default
       action, or user semantic action that manipulated yychar.
     - Of course, the expected token list depends on states to have
       correct lookahead information, and it depends on the parser not
       to perform extra reductions after fetching a lookahead from the
       scanner and before detecting a syntax error.  Thus, state merging
       (from LALR or IELR) and default reductions corrupt the expected
       token list.  However, the list is correct for canonical LR with
       one exception: it will still contain any token that will not be
       accepted due to an error action in a later state.
  */
  if (yytoken != YYEMPTY)
    {
      int yyn = yypact[*yyssp];
      yyarg[yycount++] = yytname[yytoken];
      if (!yypact_value_is_default (yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative indexes in
             YYCHECK.  In other words, skip the first -YYN actions for
             this state because they are default actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST - yyn + 1;
          int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
          int yyx;

          for (yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR
                && !yytable_value_is_error (yytable[yyx + yyn]))
              {
                if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
                  {
                    yycount = 1;
                    yysize = yysize0;
                    break;
                  }
                yyarg[yycount++] = yytname[yyx];
                {
                  YYSIZE_T yysize1 = yysize + yytnamerr (YY_NULLPTR, yytname[yyx]);
                  if (! (yysize <= yysize1
                         && yysize1 <= YYSTACK_ALLOC_MAXIMUM))
                    return 2;
                  yysize = yysize1;
                }
              }
        }
    }

  switch (yycount)
    {
# define YYCASE_(N, S)                      \
      case N:                               \
        yyformat = S;                       \
      break
      YYCASE_(0, YY_("syntax error"));
      YYCASE_(1, YY_("syntax error, unexpected %s"));
      YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
      YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
      YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
      YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
# undef YYCASE_
    }

  {
    YYSIZE_T yysize1 = yysize + yystrlen (yyformat);
    if (! (yysize <= yysize1 && yysize1 <= YYSTACK_ALLOC_MAXIMUM))
      return 2;
    yysize = yysize1;
  }

  if (*yymsg_alloc < yysize)
    {
      *yymsg_alloc = 2 * yysize;
      if (! (yysize <= *yymsg_alloc
             && *yymsg_alloc <= YYSTACK_ALLOC_MAXIMUM))
        *yymsg_alloc = YYSTACK_ALLOC_MAXIMUM;
      return 1;
    }

  /* Avoid sprintf, as that infringes on the user's name space.
     Don't have undefined behavior even if the translation
     produced a string with the wrong number of "%s"s.  */
  {
    char *yyp = *yymsg;
    int yyi = 0;
    while ((*yyp = *yyformat) != '\0')
      if (*yyp == '%' && yyformat[1] == 's' && yyi < yycount)
        {
          yyp += yytnamerr (yyp, yyarg[yyi++]);
          yyformat += 2;
        }
      else
        {
          yyp++;
          yyformat++;
        }
  }
  return 0;
}
#endif /* YYERROR_VERBOSE */

/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

static void
yydestruct (const char *yymsg, int yytype, YYSTYPE *yyvaluep)
{
  YYUSE (yyvaluep);
  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yytype, yyvaluep, yylocationp);

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  YYUSE (yytype);
  YY_IGNORE_MAYBE_UNINITIALIZED_END
}




/* The lookahead symbol.  */
int yychar;

/* The semantic value of the lookahead symbol.  */
YYSTYPE yylval;
/* Number of syntax errors so far.  */
int yynerrs;


/*----------.
| yyparse.  |
`----------*/

int
yyparse (void)
{
    int yystate;
    /* Number of tokens to shift before error messages enabled.  */
    int yyerrstatus;

    /* The stacks and their tools:
       'yyss': related to states.
       'yyvs': related to semantic values.

       Refer to the stacks through separate pointers, to allow yyoverflow
       to reallocate them elsewhere.  */

    /* The state stack.  */
    yytype_int16 yyssa[YYINITDEPTH];
    yytype_int16 *yyss;
    yytype_int16 *yyssp;

    /* The semantic value stack.  */
    YYSTYPE yyvsa[YYINITDEPTH];
    YYSTYPE *yyvs;
    YYSTYPE *yyvsp;

    YYSIZE_T yystacksize;

  int yyn;
  int yyresult;
  /* Lookahead token as an internal (translated) token number.  */
  int yytoken = 0;
  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;

#if YYERROR_VERBOSE
  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYSIZE_T yymsg_alloc = sizeof yymsgbuf;
#endif

#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  yyssp = yyss = yyssa;
  yyvsp = yyvs = yyvsa;
  yystacksize = YYINITDEPTH;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY; /* Cause a token to be read.  */
  goto yysetstate;

/*------------------------------------------------------------.
| yynewstate -- Push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
 yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;

 yysetstate:
  *yyssp = yystate;

  if (yyss + yystacksize - 1 <= yyssp)
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYSIZE_T yysize = yyssp - yyss + 1;

#ifdef yyoverflow
      {
        /* Give user a chance to reallocate the stack.  Use copies of
           these so that the &'s don't force the real ones into
           memory.  */
        YYSTYPE *yyvs1 = yyvs;
        yytype_int16 *yyss1 = yyss;

        /* Each stack pointer address is followed by the size of the
           data in use in that stack, in bytes.  This used to be a
           conditional around just the two extra args, but that might
           be undefined if yyoverflow is a macro.  */
        yyoverflow (YY_("memory exhausted"),
                    &yyss1, yysize * sizeof (*yyssp),
                    &yyvs1, yysize * sizeof (*yyvsp),
                    &yystacksize);

        yyss = yyss1;
        yyvs = yyvs1;
      }
#else /* no yyoverflow */
# ifndef YYSTACK_RELOCATE
      goto yyexhaustedlab;
# else
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
        goto yyexhaustedlab;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
        yystacksize = YYMAXDEPTH;

      {
        yytype_int16 *yyss1 = yyss;
        union yyalloc *yyptr =
          (union yyalloc *) YYSTACK_ALLOC (YYSTACK_BYTES (yystacksize));
        if (! yyptr)
          goto yyexhaustedlab;
        YYSTACK_RELOCATE (yyss_alloc, yyss);
        YYSTACK_RELOCATE (yyvs_alloc, yyvs);
#  undef YYSTACK_RELOCATE
        if (yyss1 != yyssa)
          YYSTACK_FREE (yyss1);
      }
# endif
#endif /* no yyoverflow */

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;

      YYDPRINTF ((stderr, "Stack size increased to %lu\n",
                  (unsigned long int) yystacksize));

      if (yyss + yystacksize - 1 <= yyssp)
        YYABORT;
    }

  YYDPRINTF ((stderr, "Entering state %d\n", yystate));

  if (yystate == YYFINAL)
    YYACCEPT;

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

  /* Do appropriate processing given the current state.  Read a
     lookahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to lookahead token.  */
  yyn = yypact[yystate];
  if (yypact_value_is_default (yyn))
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid lookahead symbol.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token: "));
      yychar = yylex ();
    }

  if (yychar <= YYEOF)
    {
      yychar = yytoken = YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yytable_value_is_error (yyn))
        goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the lookahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the shifted token.  */
  yychar = YYEMPTY;

  yystate = yyn;
  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END

  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- Do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     '$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
        case 2:
#line 91 "bparser.y" /* yacc.c:1646  */
    { root_node = (yyvsp[0]); }
#line 1758 "y.tab.c" /* yacc.c:1646  */
    break;

  case 3:
#line 93 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_abstract_decl((long int)(yyvsp[0]), NULL); }
#line 1764 "y.tab.c" /* yacc.c:1646  */
    break;

  case 4:
#line 94 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_abstract_decl(0, (yyvsp[0])); }
#line 1770 "y.tab.c" /* yacc.c:1646  */
    break;

  case 5:
#line 95 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_abstract_decl((long int)(yyvsp[-1]), (yyvsp[0])); }
#line 1776 "y.tab.c" /* yacc.c:1646  */
    break;

  case 7:
#line 98 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 1782 "y.tab.c" /* yacc.c:1646  */
    break;

  case 8:
#line 100 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)ADDITION; }
#line 1788 "y.tab.c" /* yacc.c:1646  */
    break;

  case 9:
#line 101 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)SUBTRACTION; }
#line 1794 "y.tab.c" /* yacc.c:1646  */
    break;

  case 10:
#line 103 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(BITAND, PRE, (yyvsp[0])); }
#line 1800 "y.tab.c" /* yacc.c:1646  */
    break;

  case 11:
#line 105 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-2]), NULL); }
#line 1806 "y.tab.c" /* yacc.c:1646  */
    break;

  case 12:
#line 106 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-3]), (yyvsp[-1])); }
#line 1812 "y.tab.c" /* yacc.c:1646  */
    break;

  case 14:
#line 109 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 1818 "y.tab.c" /* yacc.c:1646  */
    break;

  case 15:
#line 111 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTSIMPLE ; }
#line 1824 "y.tab.c" /* yacc.c:1646  */
    break;

  case 16:
#line 112 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTADDITION ; }
#line 1830 "y.tab.c" /* yacc.c:1646  */
    break;

  case 17:
#line 113 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTSUBTRACTION ; }
#line 1836 "y.tab.c" /* yacc.c:1646  */
    break;

  case 18:
#line 114 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTMULTIPLICATION ; }
#line 1842 "y.tab.c" /* yacc.c:1646  */
    break;

  case 19:
#line 115 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTDIVISION ; }
#line 1848 "y.tab.c" /* yacc.c:1646  */
    break;

  case 20:
#line 116 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTREMAINDER ; }
#line 1854 "y.tab.c" /* yacc.c:1646  */
    break;

  case 21:
#line 117 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTLEFTSHIFT ; }
#line 1860 "y.tab.c" /* yacc.c:1646  */
    break;

  case 22:
#line 118 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTRIGHTSHIFT ; }
#line 1866 "y.tab.c" /* yacc.c:1646  */
    break;

  case 23:
#line 119 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTBITWISEAND ; }
#line 1872 "y.tab.c" /* yacc.c:1646  */
    break;

  case 24:
#line 120 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTBITWISEXOR ; }
#line 1878 "y.tab.c" /* yacc.c:1646  */
    break;

  case 25:
#line 121 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTBITWISEOR ; }
#line 1884 "y.tab.c" /* yacc.c:1646  */
    break;

  case 27:
#line 124 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(BITAND, (yyvsp[-2]), (yyvsp[0])); }
#line 1890 "y.tab.c" /* yacc.c:1646  */
    break;

  case 28:
#line 127 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(BITWISENEG, PRE, (yyvsp[0])); }
#line 1896 "y.tab.c" /* yacc.c:1646  */
    break;

  case 30:
#line 130 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(BITOR, (yyvsp[-2]), (yyvsp[0])); }
#line 1902 "y.tab.c" /* yacc.c:1646  */
    break;

  case 32:
#line 133 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(BITXOR, (yyvsp[-2]), (yyvsp[0])); }
#line 1908 "y.tab.c" /* yacc.c:1646  */
    break;

  case 33:
#line 135 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_break(); }
#line 1914 "y.tab.c" /* yacc.c:1646  */
    break;

  case 35:
#line 138 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_cast_expr((yyvsp[-2]), (yyvsp[0])); }
#line 1920 "y.tab.c" /* yacc.c:1646  */
    break;

  case 36:
#line 140 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SCHAR);}
#line 1926 "y.tab.c" /* yacc.c:1646  */
    break;

  case 37:
#line 141 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SCHAR);}
#line 1932 "y.tab.c" /* yacc.c:1646  */
    break;

  case 38:
#line 142 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_UCHAR);}
#line 1938 "y.tab.c" /* yacc.c:1646  */
    break;

  case 40:
#line 145 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_comma_expr((yyvsp[-1]), (yyvsp[-2]));}
#line 1944 "y.tab.c" /* yacc.c:1646  */
    break;

  case 41:
#line 147 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_compound_statement(NULL); }
#line 1950 "y.tab.c" /* yacc.c:1646  */
    break;

  case 42:
#line 148 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_compound_statement((yyvsp[-1])); }
#line 1956 "y.tab.c" /* yacc.c:1646  */
    break;

  case 44:
#line 151 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_ternary_operation(CONDQUEST,CONDCOLON,(yyvsp[-4]), (yyvsp[-2]), (yyvsp[0])); }
#line 1962 "y.tab.c" /* yacc.c:1646  */
    break;

  case 52:
#line 163 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_continue(); }
#line 1968 "y.tab.c" /* yacc.c:1646  */
    break;

  case 53:
#line 165 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl((yyvsp[-2]), (yyvsp[-1])); }
#line 1974 "y.tab.c" /* yacc.c:1646  */
    break;

  case 56:
#line 169 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[0]); recoverable_error = 1; yyerrok;}
#line 1980 "y.tab.c" /* yacc.c:1646  */
    break;

  case 57:
#line 170 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[0]); recoverable_error = 1; yyerrok;}
#line 1986 "y.tab.c" /* yacc.c:1646  */
    break;

  case 58:
#line 172 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_statement_list(NULL, (yyvsp[0])); }
#line 1992 "y.tab.c" /* yacc.c:1646  */
    break;

  case 59:
#line 173 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_statement_list((yyvsp[-1]), (yyvsp[0])); }
#line 1998 "y.tab.c" /* yacc.c:1646  */
    break;

  case 63:
#line 180 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[-1]); }
#line 2004 "y.tab.c" /* yacc.c:1646  */
    break;

  case 64:
#line 181 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl(NULL, NULL); }
#line 2010 "y.tab.c" /* yacc.c:1646  */
    break;

  case 65:
#line 182 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl(NULL, (yyvsp[0])); }
#line 2016 "y.tab.c" /* yacc.c:1646  */
    break;

  case 66:
#line 183 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-2]), NULL); }
#line 2022 "y.tab.c" /* yacc.c:1646  */
    break;

  case 67:
#line 184 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-3]), (yyvsp[-1])); }
#line 2028 "y.tab.c" /* yacc.c:1646  */
    break;

  case 69:
#line 187 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[-1]); }
#line 2034 "y.tab.c" /* yacc.c:1646  */
    break;

  case 72:
#line 191 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_while_statement(DO_WHILE, (yyvsp[-2]), (yyvsp[-5])); }
#line 2040 "y.tab.c" /* yacc.c:1646  */
    break;

  case 74:
#line 194 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2046 "y.tab.c" /* yacc.c:1646  */
    break;

  case 75:
#line 196 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)EQ; }
#line 2052 "y.tab.c" /* yacc.c:1646  */
    break;

  case 76:
#line 197 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)NE; }
#line 2058 "y.tab.c" /* yacc.c:1646  */
    break;

  case 78:
#line 201 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_expr_list(NULL, (yyvsp[0])); }
#line 2064 "y.tab.c" /* yacc.c:1646  */
    break;

  case 79:
#line 202 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_expr_list((yyvsp[-2]), (yyvsp[0])); }
#line 2070 "y.tab.c" /* yacc.c:1646  */
    break;

  case 80:
#line 204 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_expr_statement((yyvsp[-1])); }
#line 2076 "y.tab.c" /* yacc.c:1646  */
    break;

  case 81:
#line 206 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-5]), (yyvsp[-3]), (yyvsp[-1]), NULL); }
#line 2082 "y.tab.c" /* yacc.c:1646  */
    break;

  case 82:
#line 207 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, (yyvsp[-3]), (yyvsp[-1]), NULL); }
#line 2088 "y.tab.c" /* yacc.c:1646  */
    break;

  case 83:
#line 208 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, NULL, (yyvsp[-1]), NULL); }
#line 2094 "y.tab.c" /* yacc.c:1646  */
    break;

  case 84:
#line 209 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, (yyvsp[-2]), NULL, NULL); }
#line 2100 "y.tab.c" /* yacc.c:1646  */
    break;

  case 85:
#line 210 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, NULL, NULL, NULL); }
#line 2106 "y.tab.c" /* yacc.c:1646  */
    break;

  case 86:
#line 211 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-4]), NULL, (yyvsp[-1]), NULL); }
#line 2112 "y.tab.c" /* yacc.c:1646  */
    break;

  case 87:
#line 212 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-4]), (yyvsp[-2]), NULL, NULL); }
#line 2118 "y.tab.c" /* yacc.c:1646  */
    break;

  case 88:
#line 213 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-3]), NULL, NULL, NULL); }
#line 2124 "y.tab.c" /* yacc.c:1646  */
    break;

  case 89:
#line 215 "bparser.y" /* yacc.c:1646  */
    { struct node * for_expr = (yyvsp[-1]);
                                             for_expr->data.for_statement.statement = (yyvsp[0]);
                                             (yyval) = for_expr;
                                           }
#line 2133 "y.tab.c" /* yacc.c:1646  */
    break;

  case 90:
#line 220 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_call((yyvsp[-2]), NULL); }
#line 2139 "y.tab.c" /* yacc.c:1646  */
    break;

  case 91:
#line 221 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_call((yyvsp[-3]), (yyvsp[-1])); }
#line 2145 "y.tab.c" /* yacc.c:1646  */
    break;

  case 92:
#line 224 "bparser.y" /* yacc.c:1646  */
    { 
    (yyval) = node_func_declarator((yyvsp[-3]), (yyvsp[-1])); }
#line 2152 "y.tab.c" /* yacc.c:1646  */
    break;

  case 93:
#line 228 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_def((yyvsp[-1]), (yyvsp[0])); }
#line 2158 "y.tab.c" /* yacc.c:1646  */
    break;

  case 94:
#line 232 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_def_spec((yyvsp[-1]), (yyvsp[0])); }
#line 2164 "y.tab.c" /* yacc.c:1646  */
    break;

  case 95:
#line 235 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_goto((yyvsp[-1])); }
#line 2170 "y.tab.c" /* yacc.c:1646  */
    break;

  case 96:
#line 237 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_if_else_statement((yyvsp[-4]), (yyvsp[-2]), (yyvsp[0])); }
#line 2176 "y.tab.c" /* yacc.c:1646  */
    break;

  case 97:
#line 240 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_if_statement((yyvsp[-2]), (yyvsp[0])); }
#line 2182 "y.tab.c" /* yacc.c:1646  */
    break;

  case 98:
#line 242 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(MULTIPLY, PRE, (yyvsp[0])); }
#line 2188 "y.tab.c" /* yacc.c:1646  */
    break;

  case 101:
#line 248 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl_list(NULL, (yyvsp[0])); }
#line 2194 "y.tab.c" /* yacc.c:1646  */
    break;

  case 102:
#line 249 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl_list((yyvsp[-2]), (yyvsp[0])); }
#line 2200 "y.tab.c" /* yacc.c:1646  */
    break;

  case 110:
#line 261 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_labeled_statement((yyvsp[-2]), (yyvsp[0])); }
#line 2206 "y.tab.c" /* yacc.c:1646  */
    break;

  case 112:
#line 264 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(LOGICALAND, (yyvsp[-2]), (yyvsp[0])); }
#line 2212 "y.tab.c" /* yacc.c:1646  */
    break;

  case 113:
#line 266 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(LOGNEG, PRE, (yyvsp[0])); }
#line 2218 "y.tab.c" /* yacc.c:1646  */
    break;

  case 115:
#line 269 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(LOGICALOR, (yyvsp[-2]), (yyvsp[0])); }
#line 2224 "y.tab.c" /* yacc.c:1646  */
    break;

  case 117:
#line 272 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2230 "y.tab.c" /* yacc.c:1646  */
    break;

  case 118:
#line 274 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)MULTIPLY; }
#line 2236 "y.tab.c" /* yacc.c:1646  */
    break;

  case 119:
#line 275 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)DIVIDE; }
#line 2242 "y.tab.c" /* yacc.c:1646  */
    break;

  case 120:
#line 276 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)REMAINDER; }
#line 2248 "y.tab.c" /* yacc.c:1646  */
    break;

  case 122:
#line 280 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_null_statement(); }
#line 2254 "y.tab.c" /* yacc.c:1646  */
    break;

  case 123:
#line 282 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_decl((yyvsp[-1]),(yyvsp[0])); }
#line 2260 "y.tab.c" /* yacc.c:1646  */
    break;

  case 124:
#line 283 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_decl((yyvsp[0]),NULL); }
#line 2266 "y.tab.c" /* yacc.c:1646  */
    break;

  case 125:
#line 284 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_decl((yyvsp[-1]),(yyvsp[0])); }
#line 2272 "y.tab.c" /* yacc.c:1646  */
    break;

  case 126:
#line 286 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_list(NULL, (yyvsp[0])); }
#line 2278 "y.tab.c" /* yacc.c:1646  */
    break;

  case 127:
#line 287 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_list((yyvsp[-2]), (yyvsp[0])); }
#line 2284 "y.tab.c" /* yacc.c:1646  */
    break;

  case 129:
#line 291 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[-1]); }
#line 2290 "y.tab.c" /* yacc.c:1646  */
    break;

  case 130:
#line 294 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)1; }
#line 2296 "y.tab.c" /* yacc.c:1646  */
    break;

  case 131:
#line 295 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)((long int) (yyvsp[0])+1); }
#line 2302 "y.tab.c" /* yacc.c:1646  */
    break;

  case 132:
#line 297 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_pointer_decl((long int)(yyvsp[-1]), (yyvsp[0]));
                                                  }
#line 2309 "y.tab.c" /* yacc.c:1646  */
    break;

  case 133:
#line 300 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECDECREMENT, POST, (yyvsp[-1])); }
#line 2315 "y.tab.c" /* yacc.c:1646  */
    break;

  case 139:
#line 308 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECINCREMENT, POST, (yyvsp[-1])); }
#line 2321 "y.tab.c" /* yacc.c:1646  */
    break;

  case 140:
#line 310 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECDECREMENT, PRE, (yyvsp[0])); }
#line 2327 "y.tab.c" /* yacc.c:1646  */
    break;

  case 141:
#line 312 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECINCREMENT, PRE, (yyvsp[0])); }
#line 2333 "y.tab.c" /* yacc.c:1646  */
    break;

  case 142:
#line 314 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_primary_expr((yyvsp[0])); }
#line 2339 "y.tab.c" /* yacc.c:1646  */
    break;

  case 143:
#line 315 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_primary_expr((yyvsp[0])); }
#line 2345 "y.tab.c" /* yacc.c:1646  */
    break;

  case 146:
#line 319 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2351 "y.tab.c" /* yacc.c:1646  */
    break;

  case 147:
#line 321 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)LT; }
#line 2357 "y.tab.c" /* yacc.c:1646  */
    break;

  case 148:
#line 322 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)LE; }
#line 2363 "y.tab.c" /* yacc.c:1646  */
    break;

  case 149:
#line 323 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)GT; }
#line 2369 "y.tab.c" /* yacc.c:1646  */
    break;

  case 150:
#line 324 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)GE; }
#line 2375 "y.tab.c" /* yacc.c:1646  */
    break;

  case 151:
#line 326 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_return(NULL); }
#line 2381 "y.tab.c" /* yacc.c:1646  */
    break;

  case 152:
#line 327 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_return((yyvsp[-1])); }
#line 2387 "y.tab.c" /* yacc.c:1646  */
    break;

  case 154:
#line 330 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2393 "y.tab.c" /* yacc.c:1646  */
    break;

  case 155:
#line 332 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)LEFT; }
#line 2399 "y.tab.c" /* yacc.c:1646  */
    break;

  case 156:
#line 333 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)RIGHT; }
#line 2405 "y.tab.c" /* yacc.c:1646  */
    break;

  case 157:
#line 335 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2411 "y.tab.c" /* yacc.c:1646  */
    break;

  case 158:
#line 336 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2417 "y.tab.c" /* yacc.c:1646  */
    break;

  case 159:
#line 337 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2423 "y.tab.c" /* yacc.c:1646  */
    break;

  case 160:
#line 338 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2429 "y.tab.c" /* yacc.c:1646  */
    break;

  case 161:
#line 339 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SINT);}
#line 2435 "y.tab.c" /* yacc.c:1646  */
    break;

  case 162:
#line 340 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SINT);}
#line 2441 "y.tab.c" /* yacc.c:1646  */
    break;

  case 163:
#line 341 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SINT);}
#line 2447 "y.tab.c" /* yacc.c:1646  */
    break;

  case 164:
#line 342 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2453 "y.tab.c" /* yacc.c:1646  */
    break;

  case 165:
#line 343 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2459 "y.tab.c" /* yacc.c:1646  */
    break;

  case 166:
#line 344 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2465 "y.tab.c" /* yacc.c:1646  */
    break;

  case 167:
#line 345 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2471 "y.tab.c" /* yacc.c:1646  */
    break;

  case 179:
#line 362 "bparser.y" /* yacc.c:1646  */
    { struct node * sum = node_binary_operation(ADDITION, (yyvsp[-3]), (yyvsp[-1])); 
      (yyval) = node_unary_operation(MULTIPLY, PRE, sum); 
    }
#line 2479 "y.tab.c" /* yacc.c:1646  */
    break;

  case 182:
#line 368 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[0]); recoverable_error = 1; yyerrok;}
#line 2485 "y.tab.c" /* yacc.c:1646  */
    break;

  case 183:
#line 369 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[0]); recoverable_error = 1; yyerrok;}
#line 2491 "y.tab.c" /* yacc.c:1646  */
    break;

  case 184:
#line 371 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_translation_unit(NULL, (yyvsp[0])) ; }
#line 2497 "y.tab.c" /* yacc.c:1646  */
    break;

  case 185:
#line 372 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_translation_unit((yyvsp[-1]), (yyvsp[0])); }
#line 2503 "y.tab.c" /* yacc.c:1646  */
    break;

  case 186:
#line 374 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_name((yyvsp[0]), NULL);  }
#line 2509 "y.tab.c" /* yacc.c:1646  */
    break;

  case 187:
#line 375 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_name((yyvsp[-1]), (yyvsp[0])); }
#line 2515 "y.tab.c" /* yacc.c:1646  */
    break;

  case 199:
#line 390 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(SUBTRACTION, PRE, (yyvsp[0]));}
#line 2521 "y.tab.c" /* yacc.c:1646  */
    break;

  case 200:
#line 392 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(ADDITION, PRE, (yyvsp[0]));}
#line 2527 "y.tab.c" /* yacc.c:1646  */
    break;

  case 201:
#line 395 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_USHORT);}
#line 2533 "y.tab.c" /* yacc.c:1646  */
    break;

  case 202:
#line 396 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_USHORT);}
#line 2539 "y.tab.c" /* yacc.c:1646  */
    break;

  case 203:
#line 397 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_UINT);}
#line 2545 "y.tab.c" /* yacc.c:1646  */
    break;

  case 204:
#line 398 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_UINT);}
#line 2551 "y.tab.c" /* yacc.c:1646  */
    break;

  case 205:
#line 399 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_ULONG);}
#line 2557 "y.tab.c" /* yacc.c:1646  */
    break;

  case 206:
#line 400 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_ULONG);}
#line 2563 "y.tab.c" /* yacc.c:1646  */
    break;

  case 207:
#line 402 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_VOID); }
#line 2569 "y.tab.c" /* yacc.c:1646  */
    break;

  case 208:
#line 404 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_while_statement(WHILE, (yyvsp[-2]), (yyvsp[0])); }
#line 2575 "y.tab.c" /* yacc.c:1646  */
    break;


#line 2579 "y.tab.c" /* yacc.c:1646  */
      default: break;
    }
  /* User semantic actions sometimes alter yychar, and that requires
     that yytoken be updated with the new translation.  We take the
     approach of translating immediately before every use of yytoken.
     One alternative is translating here after every semantic action,
     but that translation would be missed if the semantic action invokes
     YYABORT, YYACCEPT, or YYERROR immediately after altering yychar or
     if it invokes YYBACKUP.  In the case of YYABORT or YYACCEPT, an
     incorrect destructor might then be invoked immediately.  In the
     case of YYERROR or YYBACKUP, subsequent parser actions might lead
     to an incorrect destructor call or verbose syntax error message
     before the lookahead is translated.  */
  YY_SYMBOL_PRINT ("-> $$ =", yyr1[yyn], &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);

  *++yyvsp = yyval;

  /* Now 'shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTOKENS] + *yyssp;
  if (0 <= yystate && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTOKENS];

  goto yynewstate;


/*--------------------------------------.
| yyerrlab -- here on detecting error.  |
`--------------------------------------*/
yyerrlab:
  /* Make sure we have latest lookahead translation.  See comments at
     user semantic actions for why this is necessary.  */
  yytoken = yychar == YYEMPTY ? YYEMPTY : YYTRANSLATE (yychar);

  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
#if ! YYERROR_VERBOSE
      yyerror (YY_("syntax error"));
#else
# define YYSYNTAX_ERROR yysyntax_error (&yymsg_alloc, &yymsg, \
                                        yyssp, yytoken)
      {
        char const *yymsgp = YY_("syntax error");
        int yysyntax_error_status;
        yysyntax_error_status = YYSYNTAX_ERROR;
        if (yysyntax_error_status == 0)
          yymsgp = yymsg;
        else if (yysyntax_error_status == 1)
          {
            if (yymsg != yymsgbuf)
              YYSTACK_FREE (yymsg);
            yymsg = (char *) YYSTACK_ALLOC (yymsg_alloc);
            if (!yymsg)
              {
                yymsg = yymsgbuf;
                yymsg_alloc = sizeof yymsgbuf;
                yysyntax_error_status = 2;
              }
            else
              {
                yysyntax_error_status = YYSYNTAX_ERROR;
                yymsgp = yymsg;
              }
          }
        yyerror (yymsgp);
        if (yysyntax_error_status == 2)
          goto yyexhaustedlab;
      }
# undef YYSYNTAX_ERROR
#endif
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
         error, discard it.  */

      if (yychar <= YYEOF)
        {
          /* Return failure if at end of input.  */
          if (yychar == YYEOF)
            YYABORT;
        }
      else
        {
          yydestruct ("Error: discarding",
                      yytoken, &yylval);
          yychar = YYEMPTY;
        }
    }

  /* Else will try to reuse lookahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:

  /* Pacify compilers like GCC when the user code never invokes
     YYERROR and the label yyerrorlab therefore never appears in user
     code.  */
  if (/*CONSTCOND*/ 0)
     goto yyerrorlab;

  /* Do not reclaim the symbols of the rule whose action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;      /* Each real token shifted decrements this.  */

  for (;;)
    {
      yyn = yypact[yystate];
      if (!yypact_value_is_default (yyn))
        {
          yyn += YYTERROR;
          if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYTERROR)
            {
              yyn = yytable[yyn];
              if (0 < yyn)
                break;
            }
        }

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
        YYABORT;


      yydestruct ("Error: popping",
                  yystos[yystate], yyvsp);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END


  /* Shift the error token.  */
  YY_SYMBOL_PRINT ("Shifting", yystos[yyn], yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturn;

/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yyresult = 1;
  goto yyreturn;

#if !defined yyoverflow || YYERROR_VERBOSE
/*-------------------------------------------------.
| yyexhaustedlab -- memory exhaustion comes here.  |
`-------------------------------------------------*/
yyexhaustedlab:
  yyerror (YY_("memory exhausted"));
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
  if (yychar != YYEMPTY)
    {
      /* Make sure we have latest lookahead translation.  See comments at
         user semantic actions for why this is necessary.  */
      yytoken = YYTRANSLATE (yychar);
      yydestruct ("Cleanup: discarding lookahead",
                  yytoken, &yylval);
    }
  /* Do not reclaim the symbols of the rule whose action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
                  yystos[*yyssp], yyvsp);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
#if YYERROR_VERBOSE
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
#endif
  return yyresult;
}
#line 408 "bparser.y" /* yacc.c:1906  */

#include <stdio.h>
#include <stdlib.h>
#include "lex.yy.c"
#include "node.h"


/**
    main - run the parsing and print the output

    parameters: argc = argument count, either 0 or 2 for valid runs
                argv = array of strings of arguments

    side effects: only those of yyparse() and print_node()

**/

int main(int argc, char * argv[]) {
  int result;
  FILE * inputFile;

  if (argc == 2 || argc > 3 || (argc == 3 && strcmp(argv[1], "-f"))) {
    fprintf(stderr,"Usage: ./parser [-f FILENAME]\n");
    fprintf(stderr,"No arguments runs the parser in test mode, using stdin\n");
    fprintf(stderr,"Otherwise supply a filename for input to the parser\n");
    return 1;
  }

  if (argc == 3) {
    inputFile = fopen(argv[2], "r");
  } else {
    inputFile = stdin;
  }
 
  yyin = inputFile;
  result = yyparse();
  if (!result && !recoverable_error) {
      root_node->print_node(stdout, root_node, 0);

      fprintf(stdout, "\n--- Printing Symbol Table ---\n\n");
      root_node->traverse_node(root_node,type_node_create(NODE_COMPOUND_STATEMENT, SCOPE));
  }
  return result;
}


/**
    yyerror - print the parse error reported by yyparse()

    parameters: s = string representing the error

    side effects: prints to stderr

**/

void yyerror(char const *s) {
  fprintf(stderr, "Line %d: %s\n", yylineno, s);
}

