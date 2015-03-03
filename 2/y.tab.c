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
#line 1 "bparser.y" /* yacc.c:339  */


#define YYSTYPE struct node *
#define YYERROR_VERBOSE
#include <stdio.h>
#include "node.h"
/*enum node_type {NODE_OPERATOR, NODE_NUMBER};

struct node {
  enum node_type type;
  char *operator;
  struct node *left, *right;
  int val;
};

struct node *malloc_op_node(char *operator,
                struct node *child_left,
                struct node *child_right);
void *malloc_number_node(int val);*/
void print_tree(struct node *node_p);
int yylex(void);
int main(void);
void yyerror(char const *s);
struct node * root_node;

struct node * btest;


#line 95 "y.tab.c" /* yacc.c:339  */

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

#line 278 "y.tab.c" /* yacc.c:358  */

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
#define YYFINAL  43
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   974

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  67
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  91
/* YYNRULES -- Number of rules.  */
#define YYNRULES  205
/* YYNSTATES -- Number of states.  */
#define YYNSTATES  305

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
       0,   112,   112,   114,   115,   116,   118,   119,   121,   122,
     124,   126,   127,   129,   130,   132,   133,   134,   135,   136,
     137,   138,   139,   140,   141,   142,   144,   145,   148,   150,
     151,   153,   154,   156,   158,   159,   161,   162,   163,   165,
     166,   168,   169,   171,   172,   174,   175,   177,   178,   179,
     180,   182,   184,   186,   188,   189,   191,   192,   194,   196,
     197,   199,   200,   201,   202,   203,   205,   206,   207,   208,
     210,   212,   213,   215,   216,   218,   220,   221,   223,   225,
     226,   227,   228,   229,   230,   231,   232,   234,   239,   240,
     243,   247,   249,   250,   253,   255,   257,   260,   262,   264,
     266,   267,   269,   270,   271,   273,   274,   275,   277,   279,
     281,   282,   284,   286,   287,   289,   290,   292,   293,   294,
     296,   298,   300,   301,   302,   304,   305,   307,   309,   311,
     312,   314,   317,   319,   320,   321,   322,   323,   325,   327,
     329,   331,   332,   333,   335,   336,   338,   339,   340,   341,
     343,   344,   346,   347,   349,   350,   352,   353,   354,   355,
     356,   357,   358,   359,   360,   361,   362,   364,   366,   367,
     368,   369,   370,   371,   372,   373,   374,   375,   378,   383,
     384,   386,   387,   389,   390,   392,   393,   395,   396,   397,
     398,   399,   400,   401,   402,   403,   405,   407,   410,   411,
     412,   413,   414,   415,   417,   419
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

#define YYPACT_NINF -180

#define yypact_value_is_default(Yystate) \
  (!!((Yystate) == (-180)))

#define YYTABLE_NINF -121

#define yytable_value_is_error(Yytable_value) \
  0

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
static const yytype_int16 yypact[] =
{
      38,  -180,  -180,    25,    29,   137,   148,  -180,  -180,   -10,
      72,    58,  -180,  -180,  -180,    72,  -180,     6,  -180,  -180,
      40,  -180,   -29,  -180,  -180,  -180,  -180,    38,  -180,  -180,
    -180,  -180,  -180,  -180,  -180,    63,    66,  -180,  -180,    68,
      73,  -180,    70,  -180,    49,  -180,   -17,   617,    98,   222,
    -180,     6,  -180,  -180,  -180,  -180,  -180,  -180,  -180,    72,
     862,   862,   862,  -180,  -180,  -180,  -180,  -180,   897,   897,
     862,   862,   407,   862,  -180,    -3,  -180,    59,  -180,    71,
      57,  -180,  -180,  -180,    77,   -27,  -180,  -180,    43,  -180,
      33,   -24,  -180,  -180,    18,  -180,  -180,  -180,  -180,    90,
      46,  -180,  -180,  -180,  -180,    23,  -180,    47,    88,    60,
      76,   350,    62,    85,    75,   652,    94,  -180,   109,  -180,
    -180,  -180,    91,  -180,  -180,  -180,  -180,  -180,  -180,   286,
      72,  -180,    93,  -180,  -180,  -180,  -180,  -180,  -180,   117,
    -180,  -180,  -180,  -180,  -180,   943,  -180,  -180,  -180,  -180,
    -180,  -180,   862,  -180,  -180,  -180,  -180,     7,   107,   142,
    -180,  -180,  -180,   862,   862,   862,   862,  -180,  -180,  -180,
     862,   862,   862,   862,  -180,  -180,  -180,   862,   862,  -180,
    -180,   442,  -180,  -180,  -180,  -180,   862,  -180,  -180,   862,
     687,    23,  -180,  -180,   125,    22,    98,  -180,  -180,  -180,
     146,   722,   350,  -180,   102,   862,  -180,   103,   862,   862,
    -180,  -180,  -180,   350,  -180,  -180,  -180,  -180,  -180,  -180,
    -180,  -180,  -180,  -180,  -180,   862,     7,  -180,    35,  -180,
     862,   -24,   -27,    57,    59,    90,    71,   126,    43,  -180,
     131,  -180,  -180,    -1,    46,    -3,  -180,   132,   153,   757,
     125,  -180,   113,   792,  -180,   111,  -180,  -180,   156,  -180,
     157,  -180,  -180,  -180,  -180,   862,  -180,  -180,   862,  -180,
    -180,  -180,   139,   862,   477,   116,   827,   350,   350,  -180,
    -180,  -180,   161,  -180,   163,   512,   547,   120,   178,  -180,
     123,  -180,  -180,   171,  -180,   172,   582,   350,  -180,  -180,
    -180,  -180,   173,  -180,  -180
};

  /* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
     Performed when YYTABLE does not specify something else to do.  Zero
     means the default is an error.  */
static const yytype_uint8 yydefact[] =
{
       0,    36,   160,   163,   156,   162,   201,   204,   167,   129,
       0,     0,    69,   104,   179,     0,    92,    60,    68,   180,
       0,   185,     0,    59,   102,    66,   181,     2,    58,   103,
     186,   164,   157,    37,   161,   165,   158,    38,   200,   203,
     199,   130,     0,     1,    99,   100,     0,     0,     0,     0,
      91,   131,   182,   166,   159,   202,   198,    67,    53,     0,
       0,     0,     0,    11,    49,    47,    50,   141,     0,     0,
       0,     0,     0,     0,    48,   152,   192,    31,   191,   110,
      29,   115,    51,   142,     0,    26,   135,   193,   113,   190,
      43,     6,   143,   137,   187,   136,   195,   194,   133,    71,
     144,   134,    34,   188,   189,   123,   125,   127,     0,     0,
       0,     0,     0,     0,     0,     0,     0,    41,   141,   121,
      39,   173,    75,   170,    13,   171,   174,    54,    56,     0,
       0,   106,     0,   168,   107,   176,    46,    45,   172,     0,
     169,   108,   177,   175,    55,    34,   105,    99,   101,   197,
      10,    28,     0,   139,   140,   112,    97,   183,     0,     0,
     196,     8,     9,     0,     0,     0,     0,    12,    73,    74,
       0,     0,     0,     0,   118,   117,   119,     0,     0,   132,
     138,     0,   149,   148,   147,   146,     0,   154,   155,     0,
       0,     0,   124,   122,     4,     3,     0,    90,    33,    52,
       0,     0,     0,   120,     0,     0,   150,     0,     0,     0,
      42,    57,    78,     0,    16,    23,    25,    24,    19,    21,
      18,    20,    22,    15,    17,     0,     0,   184,     3,   128,
       0,     7,    27,    30,    32,    72,   111,     0,   114,   116,
       0,    88,    76,     0,   145,   153,    62,     0,     0,     0,
       5,   126,     0,     0,    98,     0,    87,    94,     0,   151,
       0,    40,   109,    14,    35,     0,   178,    89,     0,    63,
      61,    64,     0,     0,     0,     0,     0,     0,     0,    44,
      77,    65,     0,    83,     0,     0,     0,     0,    96,   205,
       0,    81,    82,     0,    86,     0,     0,     0,    70,    80,
      84,    85,     0,    95,    79
};

  /* YYPGOTO[NTERM-NUM].  */
static const yytype_int16 yypgoto[] =
{
    -180,  -180,   -93,     4,  -180,  -180,  -180,  -174,  -180,    28,
    -180,    24,    31,  -180,   -32,  -180,  -180,   177,   -44,  -180,
    -180,  -179,  -180,   -43,    78,  -180,   -39,    -2,  -169,   -18,
    -180,    36,  -180,   -72,  -180,  -180,  -180,  -180,  -180,  -180,
    -180,  -180,  -180,  -180,  -180,  -180,  -180,   140,  -180,  -180,
    -180,  -180,  -180,    37,  -180,  -180,    45,  -180,    96,  -180,
      10,  -180,  -180,  -180,    -4,  -180,  -180,  -180,  -180,  -180,
    -180,  -180,    41,  -180,  -180,    26,  -180,  -180,  -180,  -109,
    -180,   188,  -180,  -180,  -180,   -46,  -180,  -180,  -180,  -180,
    -180
};

  /* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,    11,   248,    75,   163,    76,    12,   120,   225,    77,
      78,    79,    80,   121,    81,    13,   122,   123,   124,   125,
      83,    84,   126,    14,   128,   129,    15,    16,   194,    17,
     131,    85,   170,   132,   243,   133,   202,   134,    86,    18,
      19,    20,   135,   136,   137,    87,   255,    45,    46,    21,
     138,   139,   140,    88,    89,    90,    91,   177,   141,   142,
     106,   107,   108,    92,    22,    23,    93,    94,    95,    96,
      97,    98,    99,   186,   143,   100,   189,    24,    25,   144,
     101,    26,    27,   159,    28,   145,   103,   104,    29,    30,
     146
};

  /* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule whose
     number is the opposite.  If YYTABLE_NINF, syntax error.  */
static const yytype_int16 yytable[] =
{
     158,   102,   200,    82,    51,    41,   127,   242,    42,   105,
     130,   247,   192,    44,   102,   102,   102,   161,   267,   168,
       8,   174,   153,   154,   102,   102,   250,   102,   149,   150,
     151,    10,   169,   157,   175,   261,    31,   176,   155,   156,
      32,   160,     1,   207,    47,   190,    58,    59,     9,     2,
       3,   263,     4,     5,     6,     7,   178,   147,    43,   250,
     190,   190,   162,   268,   227,     9,    48,   226,   179,   180,
     272,     8,     8,   190,    53,   172,    49,    54,   181,    55,
     158,     9,   191,   191,    56,   -93,   127,     8,   173,    57,
     130,   164,   166,   256,   280,   226,     9,   171,    10,   187,
     237,   195,     1,   193,   262,   165,   240,   197,   188,     2,
       3,   196,     4,     5,     6,     7,   167,   102,   102,   102,
     102,     8,   201,   198,   102,   102,   229,   102,   147,   254,
       9,   102,    10,   258,   203,   205,   260,   182,   183,   199,
     102,    33,   184,   102,   102,   239,    82,   185,    34,    35,
    -120,    36,    37,   228,   208,   209,   212,   105,   213,    38,
      39,   230,    40,   249,   252,   257,   259,   265,   288,   289,
     266,   269,   270,   273,   276,   277,   278,    51,   281,   285,
     290,   275,   291,   296,   102,   297,   298,   195,   303,    42,
     299,   300,   304,   245,   234,   236,   233,    50,   264,   148,
     232,   282,   284,   102,   287,    82,   251,   211,   231,   204,
     238,   235,   244,   293,   295,    52,     0,     0,     0,   102,
       0,   279,   228,     0,   302,   109,     1,   110,   111,     0,
     112,   113,   114,     2,     3,   115,     4,     5,     6,     7,
     116,     0,    60,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,    61,    62,     0,     0,    49,   117,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,   118,    68,    69,     0,     0,     0,     0,    70,     0,
      71,     0,    72,     0,     0,   119,     0,    73,    74,   109,
       1,   110,   111,     0,   112,   113,   114,     2,     3,   115,
       4,     5,     6,     7,   116,     0,    60,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,    61,    62,
       0,     0,    49,   210,     0,     0,    64,     0,     0,    65,
      66,     0,     0,     0,     0,   118,    68,    69,     0,     0,
       0,     0,    70,     0,    71,     0,    72,     0,     0,   119,
       0,    73,    74,   109,     0,   110,   111,     0,   112,   113,
     114,     0,     0,   115,     0,     0,     0,     0,   116,     0,
      60,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,    61,    62,     0,     0,    49,     0,     0,     0,
      64,     0,     0,    65,    66,     0,     0,     0,     0,   118,
      68,    69,     0,     0,     0,     0,    70,     0,    71,     0,
      72,     1,     0,   119,     0,    73,    74,     0,     2,     3,
       0,     4,     5,     6,     7,     0,     0,    60,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    61,
      62,     0,     0,     0,     0,     0,     0,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,   241,    60,    70,     0,    71,     0,    72,     0,     0,
       0,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,   283,    60,    70,     0,
      71,     0,    72,     0,     0,     0,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,     0,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,   292,    60,    70,     0,    71,     0,    72,     0,     0,
       0,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,   294,    60,    70,     0,
      71,     0,    72,     0,     0,     0,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,     0,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,   301,    60,    70,     0,    71,     0,    72,     0,     0,
       0,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,     0,    60,    70,     0,
      71,     0,    72,     0,     0,     0,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,    63,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,     0,    60,    70,     0,    71,     0,    72,     0,     0,
       0,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,     0,    60,    70,     0,
      71,     0,    72,     0,     0,   206,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,   246,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,     0,    60,    70,     0,    71,     0,    72,     0,     0,
       0,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,     0,    60,    70,     0,
      71,     0,    72,     0,     0,   253,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,   271,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,     0,    60,    70,     0,    71,     0,    72,     0,     0,
       0,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,     0,    60,    70,     0,
      71,     0,    72,     0,     0,   274,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,     0,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,     0,    60,    70,     0,    71,     0,    72,     0,     0,
     286,     0,    73,    74,    61,    62,     0,     0,     0,     0,
       0,     0,    64,     0,     0,    65,    66,     0,     0,     0,
       0,    67,    68,    69,     0,     0,     0,    60,    70,     0,
      71,     0,    72,     0,     0,     0,     0,    73,    74,    61,
      62,     0,     0,     0,     0,     0,     0,    64,     0,     0,
      65,    66,     0,     0,     0,     0,    67,    68,    69,     0,
       0,     0,     0,    70,     0,    71,     0,   152,     0,     0,
       0,     0,    73,    74,   214,   215,   216,   217,   218,   219,
     220,   221,   222,   223,   224
};

static const yytype_int16 yycheck[] =
{
      72,    47,   111,    47,    22,     9,    49,   181,    10,    48,
      49,   190,   105,    15,    60,    61,    62,    20,    19,    46,
      49,    45,    68,    69,    70,    71,   195,    73,    60,    61,
      62,    60,    59,    72,    58,   209,    11,    61,    70,    71,
      11,    73,     4,   115,    38,    38,    63,    64,    58,    11,
      12,   225,    14,    15,    16,    17,    38,    59,     0,   228,
      38,    38,    65,    64,   157,    58,    60,    60,    50,    51,
     249,    49,    49,    38,    11,    42,    36,    11,    60,    11,
     152,    58,    60,    60,    11,    36,   129,    49,    55,    19,
     129,    32,    35,   202,   268,    60,    58,    54,    60,    53,
     172,   105,     4,   105,   213,    34,   178,    19,    62,    11,
      12,    64,    14,    15,    16,    17,    39,   163,   164,   165,
     166,    49,    60,    63,   170,   171,    19,   173,   130,   201,
      58,   177,    60,   205,    49,    60,   208,    47,    48,    63,
     186,     4,    52,   189,   190,   177,   190,    57,    11,    12,
      41,    14,     4,   157,    60,    64,    63,   196,    41,    11,
      12,    19,    14,    38,    18,    63,    63,    41,   277,   278,
      39,    39,    19,    60,    63,    19,    19,   195,    39,    63,
      19,   253,    19,    63,   230,     7,    63,   191,   297,   191,
      19,    19,    19,   189,   166,   171,   165,    20,   230,    59,
     164,   273,   274,   249,   276,   249,   196,   129,   163,   113,
     173,   170,   186,   285,   286,    27,    -1,    -1,    -1,   265,
      -1,   265,   226,    -1,   296,     3,     4,     5,     6,    -1,
       8,     9,    10,    11,    12,    13,    14,    15,    16,    17,
      18,    -1,    20,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    32,    33,    -1,    -1,    36,    37,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    -1,    -1,    56,    -1,
      58,    -1,    60,    -1,    -1,    63,    -1,    65,    66,     3,
       4,     5,     6,    -1,     8,     9,    10,    11,    12,    13,
      14,    15,    16,    17,    18,    -1,    20,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    32,    33,
      -1,    -1,    36,    37,    -1,    -1,    40,    -1,    -1,    43,
      44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,
      -1,    -1,    56,    -1,    58,    -1,    60,    -1,    -1,    63,
      -1,    65,    66,     3,    -1,     5,     6,    -1,     8,     9,
      10,    -1,    -1,    13,    -1,    -1,    -1,    -1,    18,    -1,
      20,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    32,    33,    -1,    -1,    36,    -1,    -1,    -1,
      40,    -1,    -1,    43,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    -1,    -1,    56,    -1,    58,    -1,
      60,     4,    -1,    63,    -1,    65,    66,    -1,    11,    12,
      -1,    14,    15,    16,    17,    -1,    -1,    20,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    32,
      33,    -1,    -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    19,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    19,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    -1,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    19,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    19,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    -1,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    19,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    -1,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    -1,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    39,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    -1,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    -1,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    63,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    39,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    -1,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    -1,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    63,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    39,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    -1,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    -1,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    63,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    -1,    20,    56,    -1,    58,    -1,    60,    -1,    -1,
      63,    -1,    65,    66,    32,    33,    -1,    -1,    -1,    -1,
      -1,    -1,    40,    -1,    -1,    43,    44,    -1,    -1,    -1,
      -1,    49,    50,    51,    -1,    -1,    -1,    20,    56,    -1,
      58,    -1,    60,    -1,    -1,    -1,    -1,    65,    66,    32,
      33,    -1,    -1,    -1,    -1,    -1,    -1,    40,    -1,    -1,
      43,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      -1,    -1,    -1,    56,    -1,    58,    -1,    60,    -1,    -1,
      -1,    -1,    65,    66,    21,    22,    23,    24,    25,    26,
      27,    28,    29,    30,    31
};

  /* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,     4,    11,    12,    14,    15,    16,    17,    49,    58,
      60,    68,    73,    82,    90,    93,    94,    96,   106,   107,
     108,   116,   131,   132,   144,   145,   148,   149,   151,   155,
     156,    11,    11,     4,    11,    12,    14,     4,    11,    12,
      14,   131,    94,     0,    94,   114,   115,    38,    60,    36,
      84,    96,   148,    11,    11,    11,    11,    19,    63,    64,
      20,    32,    33,    39,    40,    43,    44,    49,    50,    51,
      56,    58,    60,    65,    66,    70,    72,    76,    77,    78,
      79,    81,    85,    87,    88,    98,   105,   112,   120,   121,
     122,   123,   130,   133,   134,   135,   136,   137,   138,   139,
     142,   147,   152,   153,   154,    93,   127,   128,   129,     3,
       5,     6,     8,     9,    10,    13,    18,    37,    49,    63,
      74,    80,    83,    84,    85,    86,    89,    90,    91,    92,
      93,    97,   100,   102,   104,   109,   110,   111,   117,   118,
     119,   125,   126,   141,   146,   152,   157,    94,   114,    81,
      81,    81,    60,   152,   152,    81,    81,    93,   100,   150,
      81,    20,    65,    71,    32,    34,    35,    39,    46,    59,
      99,    54,    42,    55,    45,    58,    61,   124,    38,    50,
      51,    60,    47,    48,    52,    57,   140,    53,    62,   143,
      38,    60,    69,    94,    95,   131,    64,    19,    63,    63,
     146,    60,   103,    49,   125,    60,    63,   100,    60,    64,
      37,    91,    63,    41,    21,    22,    23,    24,    25,    26,
      27,    28,    29,    30,    31,    75,    60,    69,   131,    19,
      19,   123,    98,    79,    76,   139,    78,   100,   120,    81,
     100,    19,    74,   101,   142,    70,    39,    88,    69,    38,
      95,   127,    18,    63,   100,   113,   146,    63,   100,    63,
     100,    74,   146,    74,    81,    41,    39,    19,    64,    39,
      19,    39,    88,    60,    63,   100,    63,    19,    19,    85,
      74,    39,   100,    19,   100,    63,    63,   100,   146,   146,
      19,    19,    19,   100,    19,   100,    63,     7,    63,    19,
      19,    19,   100,   146,    19
};

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,    67,    68,    69,    69,    69,    70,    70,    71,    71,
      72,    73,    73,    74,    74,    75,    75,    75,    75,    75,
      75,    75,    75,    75,    75,    75,    76,    76,    77,    78,
      78,    79,    79,    80,    81,    81,    82,    82,    82,    83,
      83,    84,    84,    85,    85,    86,    86,    87,    87,    87,
      87,    88,    89,    90,    91,    91,    92,    92,    93,    94,
      94,    95,    95,    95,    95,    95,    96,    96,    96,    96,
      97,    98,    98,    99,    99,   100,   101,   101,   102,   103,
     103,   103,   103,   103,   103,   103,   103,   104,   105,   105,
     106,   107,   108,   108,   109,   110,   111,   112,   113,   114,
     115,   115,   116,   116,   116,   117,   117,   117,   118,   119,
     120,   120,   121,   122,   122,   123,   123,   124,   124,   124,
     125,   126,   127,   127,   127,   128,   128,   129,   130,   131,
     131,   132,   133,   134,   134,   134,   134,   134,   135,   136,
     137,   138,   138,   138,   139,   139,   140,   140,   140,   140,
     141,   141,   142,   142,   143,   143,   144,   144,   144,   144,
     144,   144,   144,   144,   144,   144,   144,   145,   146,   146,
     146,   146,   146,   146,   146,   146,   146,   146,   147,   148,
     148,   149,   149,   150,   150,   151,   151,   152,   152,   152,
     152,   152,   152,   152,   152,   152,   153,   154,   155,   155,
     155,   155,   155,   155,   156,   157
};

  /* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     1,     1,     1,     2,     1,     3,     1,     1,
       2,     3,     4,     1,     3,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     3,     2,     1,
       3,     1,     3,     2,     1,     4,     1,     2,     2,     1,
       3,     2,     3,     1,     5,     1,     1,     1,     1,     1,
       1,     1,     2,     3,     1,     1,     1,     2,     1,     1,
       1,     3,     2,     3,     3,     4,     1,     3,     1,     1,
       7,     1,     3,     1,     1,     1,     1,     3,     2,     7,
       6,     5,     5,     4,     6,     6,     5,     3,     3,     4,
       4,     2,     1,     2,     3,     7,     5,     2,     1,     1,
       1,     3,     1,     1,     1,     1,     1,     1,     1,     3,
       1,     3,     2,     1,     3,     1,     3,     1,     1,     1,
       1,     1,     2,     1,     2,     1,     3,     1,     3,     1,
       2,     2,     2,     1,     1,     1,     1,     1,     2,     2,
       2,     1,     1,     1,     1,     3,     1,     1,     1,     1,
       2,     3,     1,     3,     1,     1,     1,     2,     2,     3,
       1,     2,     1,     1,     2,     2,     3,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     1,     1,     4,     1,
       1,     1,     2,     1,     2,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     2,     2,     3,     2,
       2,     1,     3,     2,     1,     5
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
#line 112 "bparser.y" /* yacc.c:1646  */
    { root_node = (yyvsp[0]); }
#line 1756 "y.tab.c" /* yacc.c:1646  */
    break;

  case 7:
#line 119 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 1762 "y.tab.c" /* yacc.c:1646  */
    break;

  case 8:
#line 121 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)ADDITION; }
#line 1768 "y.tab.c" /* yacc.c:1646  */
    break;

  case 9:
#line 122 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)SUBTRACTION; }
#line 1774 "y.tab.c" /* yacc.c:1646  */
    break;

  case 10:
#line 124 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(BITAND, PRE, (yyvsp[0])); }
#line 1780 "y.tab.c" /* yacc.c:1646  */
    break;

  case 11:
#line 126 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-2]), NULL); printf("ARRAY null\n"); }
#line 1786 "y.tab.c" /* yacc.c:1646  */
    break;

  case 12:
#line 127 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-3]), (yyvsp[-1])); printf("ARRAY nonull\n"); }
#line 1792 "y.tab.c" /* yacc.c:1646  */
    break;

  case 14:
#line 130 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 1798 "y.tab.c" /* yacc.c:1646  */
    break;

  case 15:
#line 132 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTSIMPLE ; }
#line 1804 "y.tab.c" /* yacc.c:1646  */
    break;

  case 16:
#line 133 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTADDITION ; }
#line 1810 "y.tab.c" /* yacc.c:1646  */
    break;

  case 17:
#line 134 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTSUBTRACTION ; }
#line 1816 "y.tab.c" /* yacc.c:1646  */
    break;

  case 18:
#line 135 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTMULTIPLICATION ; }
#line 1822 "y.tab.c" /* yacc.c:1646  */
    break;

  case 19:
#line 136 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTDIVISION ; }
#line 1828 "y.tab.c" /* yacc.c:1646  */
    break;

  case 20:
#line 137 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTREMAINDER ; }
#line 1834 "y.tab.c" /* yacc.c:1646  */
    break;

  case 21:
#line 138 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTLEFTSHIFT ; }
#line 1840 "y.tab.c" /* yacc.c:1646  */
    break;

  case 22:
#line 139 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTRIGHTSHIFT ; }
#line 1846 "y.tab.c" /* yacc.c:1646  */
    break;

  case 23:
#line 140 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTBITWISEAND ; }
#line 1852 "y.tab.c" /* yacc.c:1646  */
    break;

  case 24:
#line 141 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTBITWISEXOR ; }
#line 1858 "y.tab.c" /* yacc.c:1646  */
    break;

  case 25:
#line 142 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *) ASSIGNMENTBITWISEOR ; }
#line 1864 "y.tab.c" /* yacc.c:1646  */
    break;

  case 27:
#line 145 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(BITAND, (yyvsp[-2]), (yyvsp[0])); }
#line 1870 "y.tab.c" /* yacc.c:1646  */
    break;

  case 28:
#line 148 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(BITWISENEG, PRE, (yyvsp[0])); }
#line 1876 "y.tab.c" /* yacc.c:1646  */
    break;

  case 30:
#line 151 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(BITOR, (yyvsp[-2]), (yyvsp[0])); }
#line 1882 "y.tab.c" /* yacc.c:1646  */
    break;

  case 32:
#line 154 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(BITXOR, (yyvsp[-2]), (yyvsp[0])); }
#line 1888 "y.tab.c" /* yacc.c:1646  */
    break;

  case 33:
#line 156 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_break(); }
#line 1894 "y.tab.c" /* yacc.c:1646  */
    break;

  case 35:
#line 159 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_cast_expr((yyvsp[-2]), (yyvsp[0])); }
#line 1900 "y.tab.c" /* yacc.c:1646  */
    break;

  case 36:
#line 161 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SCHAR);}
#line 1906 "y.tab.c" /* yacc.c:1646  */
    break;

  case 37:
#line 162 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SCHAR);}
#line 1912 "y.tab.c" /* yacc.c:1646  */
    break;

  case 38:
#line 163 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_UCHAR);}
#line 1918 "y.tab.c" /* yacc.c:1646  */
    break;

  case 40:
#line 166 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_comma_expr((yyvsp[-1]), (yyvsp[-2]));}
#line 1924 "y.tab.c" /* yacc.c:1646  */
    break;

  case 41:
#line 168 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_compound_statement(NULL); }
#line 1930 "y.tab.c" /* yacc.c:1646  */
    break;

  case 42:
#line 169 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_compound_statement((yyvsp[-1])); }
#line 1936 "y.tab.c" /* yacc.c:1646  */
    break;

  case 44:
#line 172 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_ternary_operation(CONDQUEST,CONDCOLON,(yyvsp[-4]), (yyvsp[-2]), (yyvsp[0])); }
#line 1942 "y.tab.c" /* yacc.c:1646  */
    break;

  case 52:
#line 184 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_continue(); }
#line 1948 "y.tab.c" /* yacc.c:1646  */
    break;

  case 53:
#line 186 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl((yyvsp[-2]), (yyvsp[-1])); }
#line 1954 "y.tab.c" /* yacc.c:1646  */
    break;

  case 56:
#line 191 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_statement_list(NULL, (yyvsp[0])); }
#line 1960 "y.tab.c" /* yacc.c:1646  */
    break;

  case 57:
#line 192 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_statement_list((yyvsp[-1]), (yyvsp[0])); }
#line 1966 "y.tab.c" /* yacc.c:1646  */
    break;

  case 61:
#line 199 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[-1]); }
#line 1972 "y.tab.c" /* yacc.c:1646  */
    break;

  case 62:
#line 200 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl(NULL, NULL); printf("NULLZ\n");}
#line 1978 "y.tab.c" /* yacc.c:1646  */
    break;

  case 63:
#line 201 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl(NULL, (yyvsp[0])); printf("NULL after\n"); }
#line 1984 "y.tab.c" /* yacc.c:1646  */
    break;

  case 64:
#line 202 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-2]), NULL); printf("NULL before\n"); }
#line 1990 "y.tab.c" /* yacc.c:1646  */
    break;

  case 65:
#line 203 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_subscript_decl((yyvsp[-3]), (yyvsp[-1])); printf("FULLZ\n"); }
#line 1996 "y.tab.c" /* yacc.c:1646  */
    break;

  case 67:
#line 206 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[-1]); }
#line 2002 "y.tab.c" /* yacc.c:1646  */
    break;

  case 70:
#line 210 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_while_statement(DO_WHILE, (yyvsp[-2]), (yyvsp[-5])); }
#line 2008 "y.tab.c" /* yacc.c:1646  */
    break;

  case 72:
#line 213 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2014 "y.tab.c" /* yacc.c:1646  */
    break;

  case 73:
#line 215 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)EQ; }
#line 2020 "y.tab.c" /* yacc.c:1646  */
    break;

  case 74:
#line 216 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)NE; }
#line 2026 "y.tab.c" /* yacc.c:1646  */
    break;

  case 77:
#line 221 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_statement_list((yyvsp[-2]), (yyvsp[0])); }
#line 2032 "y.tab.c" /* yacc.c:1646  */
    break;

  case 78:
#line 223 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_expr_statement((yyvsp[-1])); }
#line 2038 "y.tab.c" /* yacc.c:1646  */
    break;

  case 79:
#line 225 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-5]), (yyvsp[-3]), (yyvsp[-1]), NULL); }
#line 2044 "y.tab.c" /* yacc.c:1646  */
    break;

  case 80:
#line 226 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, (yyvsp[-3]), (yyvsp[-1]), NULL); }
#line 2050 "y.tab.c" /* yacc.c:1646  */
    break;

  case 81:
#line 227 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, NULL, (yyvsp[-1]), NULL); }
#line 2056 "y.tab.c" /* yacc.c:1646  */
    break;

  case 82:
#line 228 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, (yyvsp[-2]), NULL, NULL); }
#line 2062 "y.tab.c" /* yacc.c:1646  */
    break;

  case 83:
#line 229 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement(NULL, NULL, NULL, NULL); }
#line 2068 "y.tab.c" /* yacc.c:1646  */
    break;

  case 84:
#line 230 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-4]), NULL, (yyvsp[-1]), NULL); }
#line 2074 "y.tab.c" /* yacc.c:1646  */
    break;

  case 85:
#line 231 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-4]), (yyvsp[-2]), NULL, NULL); }
#line 2080 "y.tab.c" /* yacc.c:1646  */
    break;

  case 86:
#line 232 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_for_statement((yyvsp[-3]), NULL, NULL, NULL); }
#line 2086 "y.tab.c" /* yacc.c:1646  */
    break;

  case 87:
#line 234 "bparser.y" /* yacc.c:1646  */
    { struct node * for_expr = (yyvsp[-1]);
                                             for_expr->data.for_statement.statement = (yyvsp[0]);
                                             (yyval) = for_expr;
                                           }
#line 2095 "y.tab.c" /* yacc.c:1646  */
    break;

  case 88:
#line 239 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_call((yyvsp[-2]), NULL); }
#line 2101 "y.tab.c" /* yacc.c:1646  */
    break;

  case 89:
#line 240 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_call((yyvsp[-3]), (yyvsp[-1])); }
#line 2107 "y.tab.c" /* yacc.c:1646  */
    break;

  case 90:
#line 243 "bparser.y" /* yacc.c:1646  */
    { 
    (yyval) = node_func_declarator((yyvsp[-3]), (yyvsp[-1])); }
#line 2114 "y.tab.c" /* yacc.c:1646  */
    break;

  case 91:
#line 247 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_def((yyvsp[-1]), (yyvsp[0])); }
#line 2120 "y.tab.c" /* yacc.c:1646  */
    break;

  case 92:
#line 249 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_def_spec(NULL, (yyvsp[0])); }
#line 2126 "y.tab.c" /* yacc.c:1646  */
    break;

  case 93:
#line 250 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_function_def_spec((yyvsp[-1]), (yyvsp[0])); }
#line 2132 "y.tab.c" /* yacc.c:1646  */
    break;

  case 94:
#line 253 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_goto((yyvsp[-1])); }
#line 2138 "y.tab.c" /* yacc.c:1646  */
    break;

  case 95:
#line 255 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_if_else_statement((yyvsp[-4]), (yyvsp[-2]), (yyvsp[0])); }
#line 2144 "y.tab.c" /* yacc.c:1646  */
    break;

  case 96:
#line 258 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_if_statement((yyvsp[-2]), (yyvsp[0])); }
#line 2150 "y.tab.c" /* yacc.c:1646  */
    break;

  case 97:
#line 260 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(MULTIPLY, PRE, (yyvsp[0])); }
#line 2156 "y.tab.c" /* yacc.c:1646  */
    break;

  case 100:
#line 266 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl_list(NULL, (yyvsp[0])); }
#line 2162 "y.tab.c" /* yacc.c:1646  */
    break;

  case 101:
#line 267 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl_list((yyvsp[-2]), (yyvsp[0])); }
#line 2168 "y.tab.c" /* yacc.c:1646  */
    break;

  case 109:
#line 279 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_labeled_statement((yyvsp[-2]), (yyvsp[0])); }
#line 2174 "y.tab.c" /* yacc.c:1646  */
    break;

  case 111:
#line 282 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(LOGICALAND, (yyvsp[-2]), (yyvsp[0])); }
#line 2180 "y.tab.c" /* yacc.c:1646  */
    break;

  case 112:
#line 284 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(LOGNEG, PRE, (yyvsp[0])); }
#line 2186 "y.tab.c" /* yacc.c:1646  */
    break;

  case 114:
#line 287 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation(LOGICALOR, (yyvsp[-2]), (yyvsp[0])); }
#line 2192 "y.tab.c" /* yacc.c:1646  */
    break;

  case 116:
#line 290 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2198 "y.tab.c" /* yacc.c:1646  */
    break;

  case 117:
#line 292 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)MULTIPLY; }
#line 2204 "y.tab.c" /* yacc.c:1646  */
    break;

  case 118:
#line 293 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)DIVIDE; }
#line 2210 "y.tab.c" /* yacc.c:1646  */
    break;

  case 119:
#line 294 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)REMAINDER; }
#line 2216 "y.tab.c" /* yacc.c:1646  */
    break;

  case 121:
#line 298 "bparser.y" /* yacc.c:1646  */
    { (yyval) = NULL; }
#line 2222 "y.tab.c" /* yacc.c:1646  */
    break;

  case 122:
#line 300 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_decl((yyvsp[-1]),(yyvsp[0])); }
#line 2228 "y.tab.c" /* yacc.c:1646  */
    break;

  case 123:
#line 301 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_decl((yyvsp[0]),NULL); }
#line 2234 "y.tab.c" /* yacc.c:1646  */
    break;

  case 124:
#line 302 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_decl((yyvsp[-1]),(yyvsp[0])); }
#line 2240 "y.tab.c" /* yacc.c:1646  */
    break;

  case 125:
#line 304 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_list(NULL, (yyvsp[0])); }
#line 2246 "y.tab.c" /* yacc.c:1646  */
    break;

  case 126:
#line 305 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_param_list((yyvsp[-2]), (yyvsp[0])); }
#line 2252 "y.tab.c" /* yacc.c:1646  */
    break;

  case 128:
#line 309 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (yyvsp[-1]); }
#line 2258 "y.tab.c" /* yacc.c:1646  */
    break;

  case 129:
#line 311 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)1; }
#line 2264 "y.tab.c" /* yacc.c:1646  */
    break;

  case 130:
#line 312 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)((long int) (yyvsp[0])+1); }
#line 2270 "y.tab.c" /* yacc.c:1646  */
    break;

  case 131:
#line 314 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_pointer_decl((long int)(yyvsp[-1]), (yyvsp[0]));
                                                  }
#line 2277 "y.tab.c" /* yacc.c:1646  */
    break;

  case 132:
#line 317 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECDECREMENT, POST, (yyvsp[-1])); }
#line 2283 "y.tab.c" /* yacc.c:1646  */
    break;

  case 138:
#line 325 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECINCREMENT, POST, (yyvsp[-1])); }
#line 2289 "y.tab.c" /* yacc.c:1646  */
    break;

  case 139:
#line 327 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECDECREMENT, PRE, (yyvsp[0])); }
#line 2295 "y.tab.c" /* yacc.c:1646  */
    break;

  case 140:
#line 329 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(INCDECINCREMENT, PRE, (yyvsp[0])); }
#line 2301 "y.tab.c" /* yacc.c:1646  */
    break;

  case 141:
#line 331 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_primary_expr((yyvsp[0])); }
#line 2307 "y.tab.c" /* yacc.c:1646  */
    break;

  case 142:
#line 332 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_primary_expr((yyvsp[0])); }
#line 2313 "y.tab.c" /* yacc.c:1646  */
    break;

  case 145:
#line 336 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2319 "y.tab.c" /* yacc.c:1646  */
    break;

  case 146:
#line 338 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)LT; }
#line 2325 "y.tab.c" /* yacc.c:1646  */
    break;

  case 147:
#line 339 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)LE; }
#line 2331 "y.tab.c" /* yacc.c:1646  */
    break;

  case 148:
#line 340 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)GT; }
#line 2337 "y.tab.c" /* yacc.c:1646  */
    break;

  case 149:
#line 341 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)GE; }
#line 2343 "y.tab.c" /* yacc.c:1646  */
    break;

  case 150:
#line 343 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_return(NULL); }
#line 2349 "y.tab.c" /* yacc.c:1646  */
    break;

  case 151:
#line 344 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_return((yyvsp[-1])); }
#line 2355 "y.tab.c" /* yacc.c:1646  */
    break;

  case 153:
#line 347 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_binary_operation((long int)(yyvsp[-1]), (yyvsp[-2]), (yyvsp[0])); }
#line 2361 "y.tab.c" /* yacc.c:1646  */
    break;

  case 154:
#line 349 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)LEFT; }
#line 2367 "y.tab.c" /* yacc.c:1646  */
    break;

  case 155:
#line 350 "bparser.y" /* yacc.c:1646  */
    { (yyval) = (struct node *)RIGHT; }
#line 2373 "y.tab.c" /* yacc.c:1646  */
    break;

  case 156:
#line 352 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2379 "y.tab.c" /* yacc.c:1646  */
    break;

  case 157:
#line 353 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2385 "y.tab.c" /* yacc.c:1646  */
    break;

  case 158:
#line 354 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2391 "y.tab.c" /* yacc.c:1646  */
    break;

  case 159:
#line 355 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SSHORT);}
#line 2397 "y.tab.c" /* yacc.c:1646  */
    break;

  case 160:
#line 356 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SINT);}
#line 2403 "y.tab.c" /* yacc.c:1646  */
    break;

  case 161:
#line 357 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SINT);}
#line 2409 "y.tab.c" /* yacc.c:1646  */
    break;

  case 162:
#line 358 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SINT);}
#line 2415 "y.tab.c" /* yacc.c:1646  */
    break;

  case 163:
#line 359 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2421 "y.tab.c" /* yacc.c:1646  */
    break;

  case 164:
#line 360 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2427 "y.tab.c" /* yacc.c:1646  */
    break;

  case 165:
#line 361 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2433 "y.tab.c" /* yacc.c:1646  */
    break;

  case 166:
#line 362 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_SLONG);}
#line 2439 "y.tab.c" /* yacc.c:1646  */
    break;

  case 178:
#line 379 "bparser.y" /* yacc.c:1646  */
    { struct node * sum = node_binary_operation(ADDITION, (yyvsp[-3]), (yyvsp[-1])); 
      (yyval) = node_unary_operation(MULTIPLY, PRE, sum); 
    }
#line 2447 "y.tab.c" /* yacc.c:1646  */
    break;

  case 181:
#line 386 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl_list(NULL, (yyvsp[0])) ; printf("Here I go!! :)\n"); }
#line 2453 "y.tab.c" /* yacc.c:1646  */
    break;

  case 182:
#line 387 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_decl_list((yyvsp[-1]), (yyvsp[0])); }
#line 2459 "y.tab.c" /* yacc.c:1646  */
    break;

  case 183:
#line 389 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_name((yyvsp[0]), NULL);  }
#line 2465 "y.tab.c" /* yacc.c:1646  */
    break;

  case 184:
#line 390 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_name((yyvsp[-1]), (yyvsp[0])); }
#line 2471 "y.tab.c" /* yacc.c:1646  */
    break;

  case 196:
#line 405 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(SUBTRACTION, PRE, (yyvsp[0]));}
#line 2477 "y.tab.c" /* yacc.c:1646  */
    break;

  case 197:
#line 407 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_unary_operation(ADDITION, PRE, (yyvsp[0]));}
#line 2483 "y.tab.c" /* yacc.c:1646  */
    break;

  case 198:
#line 410 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_USHORT);}
#line 2489 "y.tab.c" /* yacc.c:1646  */
    break;

  case 199:
#line 411 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_USHORT);}
#line 2495 "y.tab.c" /* yacc.c:1646  */
    break;

  case 200:
#line 412 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_UINT);}
#line 2501 "y.tab.c" /* yacc.c:1646  */
    break;

  case 201:
#line 413 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_UINT);}
#line 2507 "y.tab.c" /* yacc.c:1646  */
    break;

  case 202:
#line 414 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_ULONG);}
#line 2513 "y.tab.c" /* yacc.c:1646  */
    break;

  case 203:
#line 415 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_ULONG);}
#line 2519 "y.tab.c" /* yacc.c:1646  */
    break;

  case 204:
#line 417 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_type_spec(TYPE_VOID); }
#line 2525 "y.tab.c" /* yacc.c:1646  */
    break;

  case 205:
#line 419 "bparser.y" /* yacc.c:1646  */
    { (yyval) = node_while_statement(WHILE, (yyvsp[-2]), (yyvsp[0])); }
#line 2531 "y.tab.c" /* yacc.c:1646  */
    break;


#line 2535 "y.tab.c" /* yacc.c:1646  */
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
#line 443 "bparser.y" /* yacc.c:1906  */

#include <stdio.h>
#include <stdlib.h>
#include "lex.yy.c"
#include "node.h"

/*struct node *malloc_op_node(char *operator,
                struct node *child_left,
                struct node *child_right) {
  struct node *node_p;

  node_p = malloc(sizeof(struct node));
#ifdef DEBUG
  printf("malloc_op_node: Parser building op_node with operator %s;\n\tat address %p\n",
     operator, (void *)node_p);
#endif
  if(node_p == NULL) {
    printf("***Out of memory***\n");
  } else {
    node_p->type = NODE_OPERATOR;
    node_p->operator = operator;
    node_p->left = child_left;
    node_p->right = child_right;
  }

  return node_p;
}

void *malloc_number_node(int val) {
  struct node *node_p;

  node_p = malloc(sizeof(struct node));
#ifdef DEBUG
  printf("malloc_number_node: Parser building number_node with value %d;\n\tat address %p\n",
     val, (void *)node_p);
#endif
  if(node_p == NULL) {
    printf("***Out of memory***\n");
  } else {
    node_p->type = NODE_NUMBER;
    node_p->val = val;
  }

  return node_p;
}*/

void print_tree(struct node *node_p) {
  /*if(node_p->type == NODE_NUMBER) {
    printf("%p: Node NUMBER, Value %d\n", (void *)node_p, node_p->val);
  } else {
    printf("%p: Node OPERATOR, Operator %s, Left child %p, Right child %p\n",
       (void *)node_p, node_p->operator, (void *)node_p->left, (void *)node_p->right);
    print_tree(node_p->left);
    print_tree(node_p->right);
  }*/
}

int main(void) {
  int result;
  result = yyparse();
  if (!result) {
      root_node->print_node(stdout, root_node, 0);
  }
  return result;
}

void yyerror(char const *s) {
  fprintf(stderr, "ERROR at line %d: %s\n", yylineno, s);
}

