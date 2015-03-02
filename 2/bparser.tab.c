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


#define YYSTYPE struct lexeme *

#include <stdio.h>
#include "lexeme.h"
enum node_type {NODE_OPERATOR, NODE_NUMBER};

struct node {
  enum node_type type;
  char *operator;
  struct node *left, *right;
  int val;
};

int yylex(void);
struct node *malloc_op_node(char *operator,
                struct node *child_left,
                struct node *child_right);
void *malloc_number_node(int val);
void print_tree(struct node *node_p);
int main(void);
void yyerror(char *s);

#line 91 "bparser.tab.c" /* yacc.c:339  */

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
    RW_CONTINUE = 259,
    RW_DO = 260,
    RW_ELSE = 261,
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
    BITWISEOR = 287,
    BITXOR = 288,
    BRACELEFT = 289,
    BRACERIGHT = 290,
    BRACKETLEFT = 291,
    BRACKETRIGHT = 292,
    CHAR = 293,
    CON_CHAR = 294,
    CONDCOLON = 295,
    CONDQUEST = 296,
    CON_INT = 297,
    CONSTANT = 298,
    CON_STRING = 299,
    DIVIDE = 300,
    EQ = 301,
    GE = 302,
    GT = 303,
    IDENTIFIER = 304,
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
    PARENRIGHT = 316,
    REMAINDER = 317,
    RIGHT = 318,
    SEPSEMICOLON = 319,
    SEQUENTIALCOMMA = 320,
    SUBTRACTION = 321
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



/* Copy the second part of user declarations.  */

#line 206 "bparser.tab.c" /* yacc.c:358  */

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
#define YYFINAL  50
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   840

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  67
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  90
/* YYNRULES -- Number of rules.  */
#define YYNRULES  203
/* YYNSTATES -- Number of states.  */
#define YYNSTATES  303

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
       0,   106,   106,   107,   108,   110,   111,   113,   114,   116,
     118,   119,   121,   122,   124,   125,   126,   127,   128,   129,
     130,   131,   132,   133,   134,   136,   137,   140,   142,   143,
     145,   146,   148,   150,   151,   153,   154,   155,   157,   158,
     160,   161,   163,   164,   166,   167,   169,   170,   171,   173,
     175,   177,   179,   180,   182,   183,   185,   187,   188,   190,
     191,   192,   193,   194,   196,   197,   198,   199,   201,   203,
     204,   206,   207,   209,   211,   212,   214,   216,   217,   218,
     219,   220,   221,   222,   223,   225,   227,   228,   230,   232,
     234,   235,   239,   241,   243,   245,   247,   249,   251,   252,
     254,   255,   256,   258,   259,   260,   262,   264,   266,   267,
     269,   271,   272,   274,   275,   277,   278,   279,   281,   283,
     285,   286,   287,   289,   290,   292,   294,   296,   297,   299,
     301,   303,   304,   305,   306,   307,   309,   311,   313,   315,
     316,   317,   319,   320,   322,   323,   324,   325,   327,   328,
     330,   331,   333,   334,   336,   337,   338,   339,   340,   341,
     342,   343,   344,   345,   346,   348,   350,   351,   352,   353,
     354,   355,   356,   357,   358,   359,   361,   363,   364,   366,
     367,   369,   370,   372,   373,   375,   376,   377,   378,   379,
     380,   381,   382,   383,   385,   387,   390,   391,   392,   393,
     394,   395,   397,   399
};
#endif

#if YYDEBUG || YYERROR_VERBOSE || 0
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "RW_BREAK", "RW_CONTINUE", "RW_DO",
  "RW_ELSE", "RW_FOR", "RW_GOTO", "RW_IF", "RW_INT", "RW_LONG",
  "RW_RETURN", "RW_SHORT", "RW_SIGNED", "RW_UNSIGNED", "RW_VOID",
  "RW_WHILE", "ADDITION", "ASSIGNMENTADDITION", "ASSIGNMENTBITWISEAND",
  "ASSIGNMENTBITWISEOR", "ASSIGNMENTBITWISEXOR", "ASSIGNMENTDIVISION",
  "ASSIGNMENTLEFTSHIFT", "ASSIGNMENTMULTIPLICATION", "ASSIGNMENTREMAINDER",
  "ASSIGNMENTRIGHTSHIFT", "ASSIGNMENTSIMPLE", "ASSIGNMENTSUBTRACTION",
  "BITAND", "BITWISENEG", "BITWISEOR", "BITXOR", "BRACELEFT", "BRACERIGHT",
  "BRACKETLEFT", "BRACKETRIGHT", "CHAR", "CON_CHAR", "CONDCOLON",
  "CONDQUEST", "CON_INT", "CONSTANT", "CON_STRING", "DIVIDE", "EQ", "GE",
  "GT", "IDENTIFIER", "INCDECDECREMENT", "INCDECINCREMENT", "LE", "LEFT",
  "LOGICALAND", "LOGICALOR", "LOGNEG", "LT", "MULTIPLY", "NE", "PARENLEFT",
  "PARENRIGHT", "REMAINDER", "RIGHT", "SEPSEMICOLON", "SEQUENTIALCOMMA",
  "SUBTRACTION", "$accept", "abstract_declarator", "additive_expr",
  "add_op", "address_expr", "array_declarator", "assignment_expr",
  "assignment_op", "bitwise_and_expr", "bitwise_negation_expr",
  "bitwise_or_expr", "bitwise_xor_expr", "break_statement", "cast_expr",
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

#define YYPACT_NINF -179

#define yypact_value_is_default(Yystate) \
  (!!((Yystate) == (-179)))

#define YYTABLE_NINF -119

#define yytable_value_is_error(Yytable_value) \
  0

  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
static const yytype_int16 yypact[] =
{
     101,  -179,    10,    14,    28,    70,  -179,  -179,  -179,   -13,
      76,  -179,  -179,  -179,    76,  -179,     1,  -179,  -179,    13,
    -179,    29,  -179,  -179,  -179,  -179,    44,  -179,  -179,  -179,
    -179,  -179,  -179,    46,    58,  -179,  -179,    78,    80,  -179,
    -179,    45,    79,  -179,    12,   440,    59,   221,  -179,     1,
    -179,  -179,  -179,  -179,  -179,  -179,  -179,  -179,    76,   740,
     740,   740,  -179,  -179,  -179,  -179,  -179,   763,   763,   740,
     740,   406,   740,    -4,  -179,    94,  -179,    86,    97,  -179,
    -179,  -179,   103,     5,  -179,  -179,    84,  -179,     8,   -12,
    -179,  -179,    50,  -179,  -179,  -179,  -179,   141,    31,  -179,
    -179,  -179,  -179,   134,  -179,    89,    90,    77,    92,   349,
     100,   116,   106,   113,   112,  -179,   135,  -179,  -179,  -179,
     109,  -179,  -179,  -179,  -179,  -179,  -179,   285,    76,  -179,
     117,  -179,  -179,  -179,  -179,  -179,  -179,   136,  -179,  -179,
    -179,  -179,  -179,   811,  -179,  -179,  -179,  -179,  -179,  -179,
     740,  -179,  -179,  -179,  -179,    49,   124,   130,  -179,  -179,
    -179,   740,   740,   740,   740,  -179,  -179,  -179,   740,   740,
     740,   740,  -179,  -179,  -179,   740,   740,  -179,  -179,   463,
    -179,  -179,  -179,  -179,   740,  -179,  -179,   740,   497,   134,
    -179,  -179,   159,   -20,    59,  -179,  -179,  -179,   179,   520,
     349,  -179,   133,   740,  -179,   138,   740,   740,  -179,  -179,
    -179,   349,  -179,  -179,  -179,  -179,  -179,  -179,  -179,  -179,
    -179,  -179,  -179,   740,    49,  -179,     7,  -179,   740,   -12,
       5,    97,    94,   141,    86,   164,    84,  -179,   162,  -179,
    -179,    88,    31,    -4,  -179,   170,   147,   557,   159,  -179,
     149,   580,  -179,   146,  -179,  -179,   150,  -179,   153,  -179,
    -179,  -179,  -179,   740,  -179,  -179,   740,  -179,  -179,  -179,
     178,   740,   603,   152,   637,   349,   349,  -179,  -179,  -179,
     157,  -179,   158,   660,   694,   156,   234,  -179,   163,  -179,
    -179,   180,  -179,   181,   717,   349,  -179,  -179,  -179,  -179,
     182,  -179,  -179
};

  /* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
     Performed when YYTABLE does not specify something else to do.  Zero
     means the default is an error.  */
static const yytype_uint8 yydefact[] =
{
       0,   158,   161,   154,   160,   199,   202,    35,   165,   127,
       0,    67,   102,   177,     0,    90,    58,    66,   178,     0,
     183,     0,    57,   100,    64,   179,     0,    56,   101,   184,
     162,   155,   159,   163,   156,    36,   198,   201,   197,    37,
     128,     0,    97,    98,     0,     0,     0,     0,    89,   129,
       1,   180,   164,   157,   200,   196,    65,    51,     0,     0,
       0,     0,    10,    47,    46,    48,   139,     0,     0,     0,
       0,     0,     0,   150,   190,    30,   189,   108,    28,   113,
      49,   140,     0,    25,   133,   191,   111,   188,    42,     5,
     141,   135,   185,   134,   193,   192,   131,    69,   142,   132,
      33,   186,   187,   121,   123,   125,     0,     0,     0,     0,
       0,     0,     0,     0,     0,    40,   139,   119,    38,   171,
      73,   168,    12,   169,   172,    52,    54,     0,     0,   104,
       0,   166,   105,   174,    45,    44,   170,     0,   167,   106,
     175,   173,    53,    33,   103,    97,    99,   195,     9,    27,
       0,   137,   138,   110,    95,   181,     0,     0,   194,     7,
       8,     0,     0,     0,     0,    11,    71,    72,     0,     0,
       0,     0,   116,   115,   117,     0,     0,   130,   136,     0,
     147,   146,   145,   144,     0,   152,   153,     0,     0,     0,
     122,   120,     3,     2,     0,    88,    32,    50,     0,     0,
       0,   118,     0,     0,   148,     0,     0,     0,    41,    55,
      76,     0,    15,    22,    24,    23,    18,    20,    17,    19,
      21,    14,    16,     0,     0,   182,     2,   126,     0,     6,
      26,    29,    31,    70,   109,     0,   112,   114,     0,    86,
      74,     0,   143,   151,    60,     0,     0,     0,     4,   124,
       0,     0,    96,     0,    85,    92,     0,   149,     0,    39,
     107,    13,    34,     0,   176,    87,     0,    61,    59,    62,
       0,     0,     0,     0,     0,     0,     0,    43,    75,    63,
       0,    81,     0,     0,     0,     0,    94,   203,     0,    79,
      80,     0,    84,     0,     0,     0,    68,    78,    82,    83,
       0,    93,    77
};

  /* YYPGOTO[NTERM-NUM].  */
static const yytype_int16 yypgoto[] =
{
    -179,   -90,    57,  -179,  -179,  -179,  -171,  -179,    81,  -179,
      85,    83,  -179,   -38,  -179,  -179,   228,   -41,  -179,  -179,
    -176,  -179,   -40,   121,  -179,   -36,    -5,  -178,   -15,  -179,
      87,  -179,   -71,  -179,  -179,  -179,  -179,  -179,  -179,  -179,
    -179,  -179,  -179,  -179,  -179,  -179,   192,  -179,  -179,  -179,
    -179,  -179,    82,  -179,  -179,    96,  -179,   151,  -179,    64,
    -179,  -179,  -179,    -7,  -179,  -179,  -179,  -179,  -179,  -179,
    -179,    93,  -179,  -179,    91,  -179,  -179,  -179,  -108,  -179,
     238,  -179,  -179,  -179,   -42,  -179,  -179,  -179,  -179,  -179
};

  /* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,   246,    73,   161,    74,    11,   118,   223,    75,    76,
      77,    78,   119,    79,    12,   120,   121,   122,   123,    81,
      82,   124,    13,   126,   127,    14,    15,   192,    16,   129,
      83,   168,   130,   241,   131,   200,   132,    84,    17,    18,
      19,   133,   134,   135,    85,   253,    43,    44,    20,   136,
     137,   138,    86,    87,    88,    89,   175,   139,   140,   104,
     105,   106,    90,    21,    22,    91,    92,    93,    94,    95,
      96,    97,   184,   141,    98,   187,    23,    24,   142,    99,
      25,    26,   157,    27,   143,   101,   102,    28,    29,   144
};

  /* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule whose
     number is the opposite.  If YYTABLE_NINF, syntax error.  */
static const yytype_int16 yytable[] =
{
     156,   198,    40,   100,    80,    41,    49,   125,   240,    42,
     103,   128,   245,   190,   159,   248,   188,   100,   100,   100,
      30,   147,   148,   149,    31,   151,   152,   100,   100,     8,
     100,   153,   154,   172,   158,   155,   259,    45,    32,    33,
     189,    34,   205,   188,    50,     9,   173,    47,   248,   170,
     174,   166,   261,   145,     1,     2,    52,     3,     4,     5,
       6,    46,   160,   171,   167,   225,    35,   224,    53,     1,
       2,   270,     3,     4,     5,     6,    57,    58,     8,   156,
      36,    37,     7,    38,   185,   188,   176,   125,    54,    10,
      55,   128,   254,     8,   186,   278,   193,     7,   191,   235,
     177,   178,     9,   260,    10,   238,    56,     9,    39,   224,
     179,     1,     2,   -91,     3,     4,     5,     6,   163,   100,
     100,   100,   100,   145,   162,     8,   100,   100,   252,   100,
     164,    59,   256,   100,     9,   258,    10,   237,   169,     7,
     165,   196,   100,    60,    61,   100,   100,    80,   226,   265,
       8,   195,    63,   266,   194,    64,   197,    65,   103,     9,
     199,    10,    66,    67,    68,   201,   203,   286,   287,    69,
     188,    70,   206,    71,   207,  -118,   211,   204,    49,    72,
     273,   210,   193,     8,    41,   227,   100,   301,   180,   181,
     262,   228,     9,   182,   189,   247,   250,   255,   183,   264,
     280,   282,   257,   285,   263,   100,    80,   267,   268,   271,
     274,   275,   291,   293,   276,   279,   283,   226,   288,   289,
     294,   100,   277,   300,   107,   108,   109,   296,   110,   111,
     112,     1,     2,   113,     3,     4,     5,     6,   114,    59,
     295,   297,   298,   302,   243,   232,   231,    48,   209,   230,
     146,    60,    61,   236,   234,    47,   115,   229,   249,     7,
      63,   233,   202,    64,    51,    65,     0,     0,     0,     0,
     116,    67,    68,     0,     0,   242,     0,    69,     0,    70,
       0,    71,     0,     0,     0,   117,     0,    72,   107,   108,
     109,     0,   110,   111,   112,     1,     2,   113,     3,     4,
       5,     6,   114,    59,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,    60,    61,     0,     0,    47,
     208,     0,     0,     7,    63,     0,     0,    64,     0,    65,
       0,     0,     0,     0,   116,    67,    68,     0,     0,     0,
       0,    69,     0,    70,     0,    71,     0,     0,     0,   117,
       0,    72,   107,   108,   109,     0,   110,   111,   112,     0,
       0,   113,     0,     0,     0,     0,   114,    59,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    60,
      61,     0,     0,    47,     0,     0,     0,     0,    63,     0,
       0,    64,     0,    65,     0,     0,     0,     0,   116,    67,
      68,     0,     0,     0,     0,    69,     0,    70,     0,    71,
       0,     0,     0,   117,     0,    72,     1,     2,     0,     3,
       4,     5,     6,     0,    59,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,    60,    61,     0,     0,
       0,     0,     0,     0,     7,    63,     0,     0,    64,     0,
      65,     0,     0,     0,     0,    66,    67,    68,    59,     0,
       0,     0,    69,     0,    70,     0,    71,     0,     0,     0,
      60,    61,    72,     0,     0,     0,     0,    62,     0,    63,
       0,    59,    64,     0,    65,     0,     0,     0,     0,    66,
      67,    68,     0,    60,    61,     0,    69,     0,    70,     0,
      71,     0,    63,     0,     0,    64,    72,    65,     0,     0,
       0,     0,    66,    67,    68,    59,     0,     0,     0,    69,
       0,    70,     0,    71,   239,     0,     0,    60,    61,    72,
       0,     0,     0,     0,   244,     0,    63,     0,    59,    64,
       0,    65,     0,     0,     0,     0,    66,    67,    68,     0,
      60,    61,     0,    69,     0,    70,     0,    71,     0,    63,
       0,     0,    64,    72,    65,     0,     0,     0,     0,    66,
      67,    68,     0,     0,     0,    59,    69,     0,    70,     0,
      71,     0,     0,     0,   251,     0,    72,    60,    61,     0,
       0,     0,     0,     0,   269,     0,    63,     0,    59,    64,
       0,    65,     0,     0,     0,     0,    66,    67,    68,     0,
      60,    61,     0,    69,     0,    70,     0,    71,     0,    63,
       0,    59,    64,    72,    65,     0,     0,     0,     0,    66,
      67,    68,     0,    60,    61,     0,    69,     0,    70,     0,
      71,     0,    63,     0,   272,    64,    72,    65,     0,     0,
       0,     0,    66,    67,    68,    59,     0,     0,     0,    69,
       0,    70,     0,    71,   281,     0,     0,    60,    61,    72,
       0,     0,     0,     0,     0,     0,    63,     0,    59,    64,
       0,    65,     0,     0,     0,     0,    66,    67,    68,     0,
      60,    61,     0,    69,     0,    70,     0,    71,     0,    63,
       0,   284,    64,    72,    65,     0,     0,     0,     0,    66,
      67,    68,    59,     0,     0,     0,    69,     0,    70,     0,
      71,   290,     0,     0,    60,    61,    72,     0,     0,     0,
       0,     0,     0,    63,     0,    59,    64,     0,    65,     0,
       0,     0,     0,    66,    67,    68,     0,    60,    61,     0,
      69,     0,    70,     0,    71,   292,    63,     0,    59,    64,
      72,    65,     0,     0,     0,     0,    66,    67,    68,     0,
      60,    61,     0,    69,     0,    70,     0,    71,   299,    63,
       0,    59,    64,    72,    65,     0,     0,     0,     0,    66,
      67,    68,     0,    60,    61,     0,    69,     0,    70,     0,
      71,     0,    63,     0,     0,    64,    72,    65,     0,     0,
       0,     0,    66,    67,    68,     0,     0,     0,     0,    69,
       0,    70,     0,   150,     0,     0,     0,     0,     0,    72,
     212,   213,   214,   215,   216,   217,   218,   219,   220,   221,
     222
};

static const yytype_int16 yycheck[] =
{
      71,   109,     9,    45,    45,    10,    21,    47,   179,    14,
      46,    47,   188,   103,    18,   193,    36,    59,    60,    61,
      10,    59,    60,    61,    10,    67,    68,    69,    70,    49,
      72,    69,    70,    45,    72,    71,   207,    36,    10,    11,
      60,    13,   113,    36,     0,    58,    58,    34,   226,    41,
      62,    46,   223,    58,    10,    11,    10,    13,    14,    15,
      16,    60,    66,    55,    59,   155,    38,    60,    10,    10,
      11,   247,    13,    14,    15,    16,    64,    65,    49,   150,
      10,    11,    38,    13,    53,    36,    36,   127,    10,    60,
      10,   127,   200,    49,    63,   266,   103,    38,   103,   170,
      50,    51,    58,   211,    60,   176,    61,    58,    38,    60,
      60,    10,    11,    34,    13,    14,    15,    16,    32,   161,
     162,   163,   164,   128,    30,    49,   168,   169,   199,   171,
      33,    18,   203,   175,    58,   206,    60,   175,    54,    38,
      37,    64,   184,    30,    31,   187,   188,   188,   155,    61,
      49,    61,    39,    65,    65,    42,    64,    44,   194,    58,
      60,    60,    49,    50,    51,    49,    60,   275,   276,    56,
      36,    58,    60,    60,    65,    40,    40,    64,   193,    66,
     251,    64,   189,    49,   189,    61,   228,   295,    47,    48,
     228,    61,    58,    52,    60,    36,    17,    64,    57,    37,
     271,   272,    64,   274,    40,   247,   247,    37,    61,    60,
      64,    61,   283,   284,    61,    37,    64,   224,    61,    61,
      64,   263,   263,   294,     3,     4,     5,    64,     7,     8,
       9,    10,    11,    12,    13,    14,    15,    16,    17,    18,
       6,    61,    61,    61,   187,   164,   163,    19,   127,   162,
      58,    30,    31,   171,   169,    34,    35,   161,   194,    38,
      39,   168,   111,    42,    26,    44,    -1,    -1,    -1,    -1,
      49,    50,    51,    -1,    -1,   184,    -1,    56,    -1,    58,
      -1,    60,    -1,    -1,    -1,    64,    -1,    66,     3,     4,
       5,    -1,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    30,    31,    -1,    -1,    34,
      35,    -1,    -1,    38,    39,    -1,    -1,    42,    -1,    44,
      -1,    -1,    -1,    -1,    49,    50,    51,    -1,    -1,    -1,
      -1,    56,    -1,    58,    -1,    60,    -1,    -1,    -1,    64,
      -1,    66,     3,     4,     5,    -1,     7,     8,     9,    -1,
      -1,    12,    -1,    -1,    -1,    -1,    17,    18,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    30,
      31,    -1,    -1,    34,    -1,    -1,    -1,    -1,    39,    -1,
      -1,    42,    -1,    44,    -1,    -1,    -1,    -1,    49,    50,
      51,    -1,    -1,    -1,    -1,    56,    -1,    58,    -1,    60,
      -1,    -1,    -1,    64,    -1,    66,    10,    11,    -1,    13,
      14,    15,    16,    -1,    18,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    -1,    -1,    30,    31,    -1,    -1,
      -1,    -1,    -1,    -1,    38,    39,    -1,    -1,    42,    -1,
      44,    -1,    -1,    -1,    -1,    49,    50,    51,    18,    -1,
      -1,    -1,    56,    -1,    58,    -1,    60,    -1,    -1,    -1,
      30,    31,    66,    -1,    -1,    -1,    -1,    37,    -1,    39,
      -1,    18,    42,    -1,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    30,    31,    -1,    56,    -1,    58,    -1,
      60,    -1,    39,    -1,    -1,    42,    66,    44,    -1,    -1,
      -1,    -1,    49,    50,    51,    18,    -1,    -1,    -1,    56,
      -1,    58,    -1,    60,    61,    -1,    -1,    30,    31,    66,
      -1,    -1,    -1,    -1,    37,    -1,    39,    -1,    18,    42,
      -1,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      30,    31,    -1,    56,    -1,    58,    -1,    60,    -1,    39,
      -1,    -1,    42,    66,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    -1,    -1,    18,    56,    -1,    58,    -1,
      60,    -1,    -1,    -1,    64,    -1,    66,    30,    31,    -1,
      -1,    -1,    -1,    -1,    37,    -1,    39,    -1,    18,    42,
      -1,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      30,    31,    -1,    56,    -1,    58,    -1,    60,    -1,    39,
      -1,    18,    42,    66,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    30,    31,    -1,    56,    -1,    58,    -1,
      60,    -1,    39,    -1,    64,    42,    66,    44,    -1,    -1,
      -1,    -1,    49,    50,    51,    18,    -1,    -1,    -1,    56,
      -1,    58,    -1,    60,    61,    -1,    -1,    30,    31,    66,
      -1,    -1,    -1,    -1,    -1,    -1,    39,    -1,    18,    42,
      -1,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      30,    31,    -1,    56,    -1,    58,    -1,    60,    -1,    39,
      -1,    64,    42,    66,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    18,    -1,    -1,    -1,    56,    -1,    58,    -1,
      60,    61,    -1,    -1,    30,    31,    66,    -1,    -1,    -1,
      -1,    -1,    -1,    39,    -1,    18,    42,    -1,    44,    -1,
      -1,    -1,    -1,    49,    50,    51,    -1,    30,    31,    -1,
      56,    -1,    58,    -1,    60,    61,    39,    -1,    18,    42,
      66,    44,    -1,    -1,    -1,    -1,    49,    50,    51,    -1,
      30,    31,    -1,    56,    -1,    58,    -1,    60,    61,    39,
      -1,    18,    42,    66,    44,    -1,    -1,    -1,    -1,    49,
      50,    51,    -1,    30,    31,    -1,    56,    -1,    58,    -1,
      60,    -1,    39,    -1,    -1,    42,    66,    44,    -1,    -1,
      -1,    -1,    49,    50,    51,    -1,    -1,    -1,    -1,    56,
      -1,    58,    -1,    60,    -1,    -1,    -1,    -1,    -1,    66,
      19,    20,    21,    22,    23,    24,    25,    26,    27,    28,
      29
};

  /* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
static const yytype_uint8 yystos[] =
{
       0,    10,    11,    13,    14,    15,    16,    38,    49,    58,
      60,    72,    81,    89,    92,    93,    95,   105,   106,   107,
     115,   130,   131,   143,   144,   147,   148,   150,   154,   155,
      10,    10,    10,    11,    13,    38,    10,    11,    13,    38,
     130,    93,    93,   113,   114,    36,    60,    34,    83,    95,
       0,   147,    10,    10,    10,    10,    61,    64,    65,    18,
      30,    31,    37,    39,    42,    44,    49,    50,    51,    56,
      58,    60,    66,    69,    71,    75,    76,    77,    78,    80,
      84,    86,    87,    97,   104,   111,   119,   120,   121,   122,
     129,   132,   133,   134,   135,   136,   137,   138,   141,   146,
     151,   152,   153,    92,   126,   127,   128,     3,     4,     5,
       7,     8,     9,    12,    17,    35,    49,    64,    73,    79,
      82,    83,    84,    85,    88,    89,    90,    91,    92,    96,
      99,   101,   103,   108,   109,   110,   116,   117,   118,   124,
     125,   140,   145,   151,   156,    93,   113,    80,    80,    80,
      60,   151,   151,    80,    80,    92,    99,   149,    80,    18,
      66,    70,    30,    32,    33,    37,    46,    59,    98,    54,
      41,    55,    45,    58,    62,   123,    36,    50,    51,    60,
      47,    48,    52,    57,   139,    53,    63,   142,    36,    60,
      68,    93,    94,   130,    65,    61,    64,    64,   145,    60,
     102,    49,   124,    60,    64,    99,    60,    65,    35,    90,
      64,    40,    19,    20,    21,    22,    23,    24,    25,    26,
      27,    28,    29,    74,    60,    68,   130,    61,    61,   122,
      97,    78,    75,   138,    77,    99,   119,    80,    99,    61,
      73,   100,   141,    69,    37,    87,    68,    36,    94,   126,
      17,    64,    99,   112,   145,    64,    99,    64,    99,    73,
     145,    73,    80,    40,    37,    61,    65,    37,    61,    37,
      87,    60,    64,    99,    64,    61,    61,    84,    73,    37,
      99,    61,    99,    64,    64,    99,   145,   145,    61,    61,
      61,    99,    61,    99,    64,     6,    64,    61,    61,    61,
      99,   145,    61
};

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const yytype_uint8 yyr1[] =
{
       0,    67,    68,    68,    68,    69,    69,    70,    70,    71,
      72,    72,    73,    73,    74,    74,    74,    74,    74,    74,
      74,    74,    74,    74,    74,    75,    75,    76,    77,    77,
      78,    78,    79,    80,    80,    81,    81,    81,    82,    82,
      83,    83,    84,    84,    85,    85,    86,    86,    86,    87,
      88,    89,    90,    90,    91,    91,    92,    93,    93,    94,
      94,    94,    94,    94,    95,    95,    95,    95,    96,    97,
      97,    98,    98,    99,   100,   100,   101,   102,   102,   102,
     102,   102,   102,   102,   102,   103,   104,   104,   105,   106,
     107,   107,   108,   109,   110,   111,   112,   113,   114,   114,
     115,   115,   115,   116,   116,   116,   117,   118,   119,   119,
     120,   121,   121,   122,   122,   123,   123,   123,   124,   125,
     126,   126,   126,   127,   127,   128,   129,   130,   130,   131,
     132,   133,   133,   133,   133,   133,   134,   135,   136,   137,
     137,   137,   138,   138,   139,   139,   139,   139,   140,   140,
     141,   141,   142,   142,   143,   143,   143,   143,   143,   143,
     143,   143,   143,   143,   143,   144,   145,   145,   145,   145,
     145,   145,   145,   145,   145,   145,   146,   147,   147,   148,
     148,   149,   149,   150,   150,   151,   151,   151,   151,   151,
     151,   151,   151,   151,   152,   153,   154,   154,   154,   154,
     154,   154,   155,   156
};

  /* YYR2[YYN] -- Number of symbols on the right hand side of rule YYN.  */
static const yytype_uint8 yyr2[] =
{
       0,     2,     1,     1,     2,     1,     3,     1,     1,     2,
       3,     4,     1,     3,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     3,     2,     1,     3,
       1,     3,     2,     1,     4,     1,     2,     2,     1,     3,
       2,     3,     1,     5,     1,     1,     1,     1,     1,     1,
       2,     3,     1,     1,     1,     2,     1,     1,     1,     3,
       2,     3,     3,     4,     1,     3,     1,     1,     7,     1,
       3,     1,     1,     1,     1,     3,     2,     7,     6,     5,
       5,     4,     6,     6,     5,     3,     3,     4,     4,     2,
       1,     2,     3,     7,     5,     2,     1,     1,     1,     3,
       1,     1,     1,     1,     1,     1,     1,     3,     1,     3,
       2,     1,     3,     1,     3,     1,     1,     1,     1,     1,
       2,     1,     2,     1,     3,     1,     3,     1,     2,     2,
       2,     1,     1,     1,     1,     1,     2,     2,     2,     1,
       1,     1,     1,     3,     1,     1,     1,     1,     2,     3,
       1,     3,     1,     1,     1,     2,     2,     3,     1,     2,
       1,     1,     2,     2,     3,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     1,     1,     4,     1,     1,     1,
       2,     1,     2,     1,     1,     1,     1,     1,     1,     1,
       1,     1,     1,     1,     2,     2,     3,     2,     2,     1,
       3,     2,     1,     5
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
      
#line 1654 "bparser.tab.c" /* yacc.c:1646  */
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
#line 423 "bparser.y" /* yacc.c:1906  */

#include <stdio.h>
#include <stdlib.h>
#include "lex.yy.c"
#include "lexeme.h"
struct node *malloc_op_node(char *operator,
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
}

void print_tree(struct node *node_p) {
  if(node_p->type == NODE_NUMBER) {
    printf("%p: Node NUMBER, Value %d\n", (void *)node_p, node_p->val);
  } else {
    printf("%p: Node OPERATOR, Operator %s, Left child %p, Right child %p\n",
       (void *)node_p, node_p->operator, (void *)node_p->left, (void *)node_p->right);
    print_tree(node_p->left);
    print_tree(node_p->right);
  }
}

int main(void) {
  return yyparse();
}

void yyerror(char *s) {
  fprintf(stderr, "%s\n", s);
}

