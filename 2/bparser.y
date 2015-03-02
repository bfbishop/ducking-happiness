%{

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

%}

%start program

%token RW_BREAK RW_CHAR RW_CONTINUE RW_DO RW_FOR RW_GOTO RW_IF RW_INT RW_LONG RW_RETURN RW_SHORT RW_SIGNED RW_UNSIGNED RW_VOID RW_WHILE

%token ADDITION
%token ASSIGNMENTADDITION
%token ASSIGNMENTBITWISEAND
%token ASSIGNMENTBITWISEOR
%token ASSIGNMENTBITWISEXOR
%token ASSIGNMENTDIVISION
%token ASSIGNMENTLEFTSHIFT
%token ASSIGNMENTMULTIPLICATION
%token ASSIGNMENTREMAINDER
%token ASSIGNMENTRIGHTSHIFT
%token ASSIGNMENTSIMPLE
%token ASSIGNMENTSUBTRACTION
%token BITAND
%token BITWISENEG
%token BITOR
%token BITXOR
%token BRACELEFT
%token BRACERIGHT
%token BRACKETLEFT
%token BRACKETRIGHT
%token CHAR
%token CONDCOLON
%token CONDQUEST
%token INT
%token STRING
%token DIVIDE
%token EQ
%token GE
%token GT
%token ID
%token INCDECDECREMENT
%token INCDECINCREMENT
%token LE
%token LEFT
%token LOGICALAND
%token LOGICALOR
%token LOGNEG
%token LT
%token MULTIPLY
%token NE
%token PARENLEFT
%token PARENRIGHT RW_ELSE
%token REMAINDER
%token RIGHT
%token SEPSEMICOLON
%token SEQUENTIALCOMMA
%token SUBTRACTION
%token ULONG

/*%token RW_BREAK RW_CHAR RW_CONTINUE RW_DO RW_ELSE RW_FOR RW_GOTO RW_IF RW_INT RW_LONG RW_RETURN RW_SHORT RW_SIGNED RW_UNSIGNED RW_VOID RW_WHILE
%token ID
%token NUMBER
%token STRING
%token SEQUENTIALOP_COMMA
%token ASSIGNOP_SIMPLE ASSIGNOP_ADDITION ASSIGNOP_SUBTRACTION ASSIGNOP_MULTIPLICATION ASSIGNOP_DIVISION ASSIGNOP_REMAINDER ASSIGNOP_LEFTSHIFT ASSIGNOP_RIGHTSHIFT ASSIGNOP_BITWISEAND ASSIGNOP_BITWISEXOR ASSIGNOP_BITWISEOR
%token CONDITIONAL_QUEST CONDITIONAL_COLON
%token LOGICALOP_OR LOGICALOP_AND
%token BITWISEOP_OR BITWISEOP_XOR BITWISEOP_AND
%token EQUALOP_EQ EQUALOP_NE
%token RELOP_LT RELOP_LE RELOP_GT RELOP_GE
%token SHIFTOP_LEFT SHIFTOP_RIGHT
%token ADDOP_ADDITION ADDOP_SUBTRACTION
%token MULOP_MULTIPLY MULOP_DIVIDE MULOP_REMAINDER
%token BITWISENEG
%token LOGNEG
%token INCDEC_INCREMENT INCDEC_DECREMENT
%token STMTSEP_SEMICOLONo
%token BRACE_LEFT BRACE_RIGHT
%token BRACKET_LEFT BRACKET_RIGHT
%token PAREN_LEFT PAREN_RIGHT

%left ADDOP_ADDITION
%left MULOP_MULTIPLY*/
%%

program : translation_unit { root_node = $1; }

abstract_declarator :  pointer 
    |	 direct_abstract_declarator
	|	 pointer direct_abstract_declarator  { struct node * decl = $2;
                                               decl->data.type.pointer_depth=(long int)$1;
                                               $$ = decl;
                                             }
;
additive_expr :  multiplicative_expr 
	|	 additive_expr add_op multiplicative_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
add_op :  ADDITION { $$ = (struct node *)ADDITION; }
	|	 SUBTRACTION { $$ = (struct node *)SUBTRACTION; }
;
address_expr :  BITAND cast_expr { $$ = node_unary_operation(BITAND, $2); } 
;
array_declarator :  direct_declarator BRACKETLEFT BRACKETRIGHT { $$ = node_subscript_statement($1, NULL); }
    |    direct_declarator BRACKETLEFT constant_expr BRACKETRIGHT  { $$ = node_subscript_statement($1, $3); } 
;
assignment_expr :  conditional_expr 
	|	 unary_expr assignment_op assignment_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
assignment_op :  ASSIGNMENTSIMPLE { $$ = (struct node *) ASSIGNMENTSIMPLE ; } 
	|	 ASSIGNMENTADDITION { $$ = (struct node *) ASSIGNMENTADDITION ; } 
	|	 ASSIGNMENTSUBTRACTION { $$ = (struct node *) ASSIGNMENTSUBTRACTION ; } 
	|	 ASSIGNMENTMULTIPLICATION { $$ = (struct node *) ASSIGNMENTMULTIPLICATION ; } 
	|	 ASSIGNMENTDIVISION { $$ = (struct node *) ASSIGNMENTDIVISION ; } 
	|	 ASSIGNMENTREMAINDER { $$ = (struct node *) ASSIGNMENTREMAINDER ; } 
    |    ASSIGNMENTLEFTSHIFT { $$ = (struct node *) ASSIGNMENTLEFTSHIFT ; } 
	|	 ASSIGNMENTRIGHTSHIFT { $$ = (struct node *) ASSIGNMENTRIGHTSHIFT ; } 
	|	 ASSIGNMENTBITWISEAND { $$ = (struct node *) ASSIGNMENTBITWISEAND ; } 
	|	 ASSIGNMENTBITWISEXOR { $$ = (struct node *) ASSIGNMENTBITWISEXOR ; } 
	|	 ASSIGNMENTBITWISEOR { $$ = (struct node *) ASSIGNMENTBITWISEOR ; } 
;
bitwise_and_expr :  equality_expr 
	|	 bitwise_and_expr BITAND equality_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;

bitwise_negation_expr :  BITWISENEG cast_expr { $$ = node_unary_operation((long int)$1, $2); }
;
bitwise_or_expr :  bitwise_xor_expr 
	|	 bitwise_or_expr BITOR bitwise_xor_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
bitwise_xor_expr :  bitwise_and_expr 
	|	 bitwise_xor_expr BITXOR bitwise_and_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
break_statement :  RW_BREAK SEPSEMICOLON { $$ = node_break(); }
;
cast_expr :  unary_expr 
	|	 PARENLEFT type_name PARENRIGHT cast_expr { $$ = node_cast_expr($2, $4); }
;
character_type_specifier :  RW_CHAR { $$ = node_type(TYPE_SCHAR, 0, NULL);}
	|	 RW_SIGNED RW_CHAR { $$ = node_type(TYPE_SCHAR, 0, NULL);}
	|	 RW_UNSIGNED RW_CHAR { $$ = node_type(TYPE_UCHAR, 0, NULL);}
;
comma_expr :  assignment_expr 
	|	 comma_expr SEQUENTIALCOMMA assignment_expr { $$ = node_comma_expr($2, $1);}
;
compound_statement :  BRACELEFT BRACERIGHT 
    |    BRACELEFT declaration_or_statement_list BRACERIGHT { $$ = $2; }
;
conditional_expr :  logical_or_expr 
	|	 logical_or_expr CONDQUEST expr CONDCOLON conditional_expr { $$ = node_ternary_operation(CONDQUEST,CONDCOLON,$1, $3, $5); }
;
conditional_statement :  if_statement 
	|	 if_else_statement 
;
constant :  INT 
    |       ULONG
    |       CHAR
	|  	    STRING
;
constant_expr :  conditional_expr 
;
continue_statement :  RW_CONTINUE SEPSEMICOLON { $$ = node_continue(); }
;
decl :  declaration_specifiers initialized_declarator_list SEPSEMICOLON { $$ = node_decl($1, $2); }
;
declaration_or_statement :  decl 
	|	 statement 
;
declaration_or_statement_list :  declaration_or_statement { $$ = node_statement_list(NULL, $1); }
	|	 declaration_or_statement_list declaration_or_statement { $$ = node_statement_list($1, $2); }
;
declaration_specifiers :   type_specifier 
;
declarator :  pointer_declarator 
	|	 direct_declarator 
;
direct_abstract_declarator :  PARENLEFT abstract_declarator PARENRIGHT { $$ = $2; }
	|	 BRACKETLEFT BRACKETRIGHT { $$ = NULL; }
	|	 BRACKETLEFT constant_expr BRACKETRIGHT { $$ = $2; }
	|	 direct_abstract_declarator BRACKETLEFT BRACKETRIGHT 
	|	 direct_abstract_declarator BRACKETLEFT constant_expr BRACKETRIGHT { $$ = node_subscript_statement($1, $3); }
;
direct_declarator :  simple_declarator 
	|	 PARENLEFT declarator PARENRIGHT { $$ = $2; }
	|	 function_declarator  
	|	 array_declarator 
;
do_statement :  RW_DO statement RW_WHILE PARENLEFT expr PARENRIGHT SEPSEMICOLON { $$ = node_while_statement(DO_WHILE, $5, $3); }
;
equality_expr :  relational_expr 
	|	 equality_expr equality_op relational_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
equality_op :  EQ { $$ = (struct node *)EQ; }
	|	 NE { $$ = (struct node *)NE; }
;
expr :  comma_expr 
;
expression_list :  assignment_expr 
	|	 expression_list SEQUENTIALCOMMA assignment_expr { $$ = node_statement_list($1, $3); }
;
expression_statement :  expr SEPSEMICOLON 
;
for_expr :  PARENLEFT initial_clause SEPSEMICOLON expr SEPSEMICOLON expr PARENRIGHT { $$ = node_for_statement($2, $4, $6, NULL); } 
    |    PARENLEFT SEPSEMICOLON expr SEPSEMICOLON expr PARENRIGHT { $$ = node_for_statement(NULL, $3, $5, NULL); }
    |    PARENLEFT SEPSEMICOLON SEPSEMICOLON expr PARENRIGHT { $$ = node_for_statement(NULL, NULL, $4, NULL); }
    |    PARENLEFT SEPSEMICOLON expr SEPSEMICOLON PARENRIGHT { $$ = node_for_statement(NULL, $3, NULL, NULL); }
    |    PARENLEFT SEPSEMICOLON SEPSEMICOLON PARENRIGHT { $$ = node_for_statement(NULL, NULL, NULL, NULL); }
    |    PARENLEFT initial_clause SEPSEMICOLON SEPSEMICOLON expr PARENRIGHT { $$ = node_for_statement($2, NULL, $5, NULL); }
    |    PARENLEFT initial_clause SEPSEMICOLON expr SEPSEMICOLON PARENRIGHT { $$ = node_for_statement($2, $4, NULL, NULL); }
    |    PARENLEFT initial_clause SEPSEMICOLON SEPSEMICOLON PARENRIGHT { $$ = node_for_statement($2, NULL, NULL, NULL); }
;
for_statement :  RW_FOR for_expr statement { struct node * for_expr = $2;
                                             for_expr->data.for_statement.statement = $3;
                                             $$ = for_expr;
                                           }
;
function_call :  postfix_expr PARENLEFT PARENRIGHT { $$ = node_function_call($1, NULL); }
    |   postfix_expr PARENLEFT expression_list PARENRIGHT { $$ = node_function_call($1, $2); }
;

function_declarator :  direct_declarator PARENLEFT parameter_type_list PARENRIGHT { 
    $$ = node_func_declarator($1, $3); } 
;

function_definition :  function_def_specifier compound_statement { $$ = node_function_def($1, $2); }
;
function_def_specifier :  declarator { $$ = node_function_def_spec(NULL, $1); }
    |   declaration_specifiers declarator { struct node * decl_spec;
                                            decl_spec = $1;
                                            decl_spec->data.type.id = $2->data.func_declarator.decl;
                                            $$ = node_function_def_spec($1, $2); }
;


goto_statement :  RW_GOTO named_label SEPSEMICOLON { $$ = node_goto($2); }
;
if_else_statement :  RW_IF PARENLEFT expr PARENRIGHT statement RW_ELSE statement { $$ = node_if_else_statement($3, $5, $7); }
;
if_statement :  RW_IF PARENLEFT expr PARENRIGHT statement { $$ = node_if_statement($3, $5); }
;
indirection_expr : MULTIPLY cast_expr { $$ = node_unary_operation(MULTIPLY, $2); }
;
initial_clause :  expr 
;
initialized_declarator :  declarator 
;
initialized_declarator_list :  initialized_declarator
	|	 initialized_declarator_list SEQUENTIALCOMMA initialized_declarator 
;
integer_type_specifier :  signed_type_specifier 
	|	 unsigned_type_specifier 
	|	 character_type_specifier 
;
iterative_statement :  while_statement 
	|	 do_statement 
	|	 for_statement 
;
label :  named_label 
;
labeled_statement :  label CONDCOLON statement { $$ = node_labeled_statement($1, $3); }
;
logical_and_expr :  bitwise_or_expr 
	|	 logical_and_expr LOGICALAND bitwise_or_expr { $$ = node_binary_operation(LOGICALAND, $1, $3); }
;
logical_negation_expr :  LOGNEG cast_expr { $$ = node_unary_operation(LOGNEG, $2); }
;
logical_or_expr :  logical_and_expr 
	|	 logical_or_expr LOGICALOR logical_and_expr { $$ = node_binary_operation(LOGICALOR, $1, $3); }
;
multiplicative_expr :  cast_expr 
	|	 multiplicative_expr mult_op cast_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
mult_op :  MULTIPLY { $$ = (struct node *)MULTIPLY; }
	|	 DIVIDE { $$ = (struct node *)DIVIDE; }
	|	 REMAINDER { $$ = (struct node *)REMAINDER; }
;
named_label :  ID 
;
null_statement :  SEPSEMICOLON { $$ = NULL; }
;
parameter_decl :  declaration_specifiers declarator 
	|	 declaration_specifiers 
	|	 declaration_specifiers abstract_declarator
;
parameter_list :  parameter_decl { $$ = node_param_list(NULL, $1); }
	|	 parameter_list SEQUENTIALCOMMA parameter_decl { $$ = node_param_list($3, $1); }
;
parameter_type_list :  parameter_list 
;
parenthesized_expr :  PARENLEFT expr PARENRIGHT { $$ = $2; }
;
pointer :  MULTIPLY { $$ = (struct node *)1; }
	|	 MULTIPLY pointer { $$ = (struct node *)((long int) $1)+1; }
;
pointer_declarator :  pointer direct_declarator   { struct node * decl = $2;
                                                  decl->data.type.pointer_depth=(long int)$1;
                                                  $$ = decl;
                                                  }
;
postdecrement_expr :  postfix_expr INCDECDECREMENT { $$ = node_unary_operation(INCDECDECREMENT, $1); }
;
postfix_expr :  primary_expr 
	|	 subscript_expr 
    |    function_call 
	|	 postincrement_expr 
	|	 postdecrement_expr 
;
postincrement_expr :  postfix_expr INCDECINCREMENT { $$ = node_unary_operation(INCDECINCREMENT, $1); }
;
predecrement_expr :  INCDECDECREMENT unary_expr { $$ = node_unary_operation(INCDECDECREMENT, $2); }
;
preincrement_expr :  INCDECINCREMENT unary_expr { $$ = node_unary_operation(INCDECINCREMENT, $2); } 
;
primary_expr :  ID 
	|	 constant 
	|	 parenthesized_expr
;
relational_expr :  shift_expr 
	|	 relational_expr relational_op shift_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
relational_op :  LT { $$ = (struct node *)LT; }
	|	 LE { $$ = (struct node *)LE; } 
	|	 GT { $$ = (struct node *)GT; }
	|	 GE { $$ = (struct node *)GE; }
;
return_statement :  RW_RETURN SEPSEMICOLON { $$ = node_return(NULL); }
    |  RW_RETURN expr SEPSEMICOLON { $$ = node_return($2); }
;
shift_expr :  additive_expr 
	|	 shift_expr shift_op additive_expr { $$ = node_binary_operation((long int)$2, $1, $3); } 
;
shift_op :  LEFT { $$ = (struct node *)LEFT; }
	|	 RIGHT { $$ = (struct node *)RIGHT; }
;
signed_type_specifier :  RW_SHORT { $$ = node_type(TYPE_SSHORT, 0, NULL);} 
	|	 RW_SHORT RW_INT { $$ = node_type(TYPE_SSHORT, 0, NULL);} 
	|	 RW_SIGNED RW_SHORT { $$ = node_type(TYPE_SSHORT, 0, NULL);}
	|	 RW_SIGNED RW_SHORT RW_INT { $$ = node_type(TYPE_SSHORT, 0, NULL);}
	|	 RW_INT { $$ = node_type(TYPE_SINT, 0, NULL);}
	|	 RW_SIGNED RW_INT { $$ = node_type(TYPE_SINT, 0, NULL);}
	|	 RW_SIGNED { $$ = node_type(TYPE_SINT, 0, NULL);}
	|	 RW_LONG { $$ = node_type(TYPE_SLONG, 0, NULL);}
	|	 RW_LONG RW_INT { $$ = node_type(TYPE_SLONG, 0, NULL);}
	|	 RW_SIGNED RW_LONG { $$ = node_type(TYPE_SLONG, 0, NULL);}
	|	 RW_SIGNED RW_LONG RW_INT { $$ = node_type(TYPE_SLONG, 0, NULL);}
;
simple_declarator :  ID 
;
statement :  expression_statement 
	|	 labeled_statement 
	|	 compound_statement 
	|	 conditional_statement 
	|	 iterative_statement 
    |    break_statement 
	|	 continue_statement 
	|	 return_statement 
	|	 goto_statement 
	|	 null_statement 
;
subscript_expr : postfix_expr BRACKETLEFT expr BRACKETRIGHT { $$ = node_subscript_statement($1, $3); }
;
top_level_decl : decl
    |   function_definition 
;
translation_unit :  top_level_decl { $$ = node_statement_list(NULL, $1) ; printf("Here I go!! :)\n"); }
	|	 translation_unit top_level_decl { $$ = node_statement_list($1, $2); }
;
type_name :  declaration_specifiers 
    |   declaration_specifiers abstract_declarator 
;
type_specifier : integer_type_specifier 
	|	void_type_specifier 
;
unary_expr :  postfix_expr 
	|    unary_minus_expr 
	|	 unary_plus_expr 
	|	 logical_negation_expr 
	|	 bitwise_negation_expr 
	|	 address_expr 
	|	 indirection_expr 
	|	 preincrement_expr 
	|	 predecrement_expr 
;
unary_minus_expr : SUBTRACTION cast_expr { $$ = node_unary_operation(SUBTRACTION, $2);}
;
unary_plus_expr : ADDITION cast_expr { $$ = node_unary_operation(ADDITION, $2);}
;
unsigned_type_specifier :  
         RW_UNSIGNED RW_SHORT RW_INT { $$ = node_type(TYPE_USHORT, 0, NULL);}
    |    RW_UNSIGNED RW_SHORT { $$ = node_type(TYPE_USHORT, 0, NULL);}
	|	 RW_UNSIGNED RW_INT { $$ = node_type(TYPE_UINT, 0, NULL);}
	|	 RW_UNSIGNED { $$ = node_type(TYPE_UINT, 0, NULL);}
	|	 RW_UNSIGNED RW_LONG RW_INT { $$ = node_type(TYPE_ULONG, 0, NULL);}
	|	 RW_UNSIGNED RW_LONG { $$ = node_type(TYPE_ULONG, 0, NULL);}
;
void_type_specifier :  RW_VOID { $$ = node_type(TYPE_VOID,0,NULL); }
;
while_statement :  RW_WHILE PARENLEFT expr PARENRIGHT statement { $$ = node_while_statement(WHILE, $3, $5); }
;


/*line   : expr STMTSEP_SEMICOLON      { print_tree((struct node *)$1);
                                       printf("\n"); }
;

expr   : expr ADDOP_ADDITION term    { $$ = (long)malloc_op_node("+",
                         (struct node *)$1,
                         (struct node *)$3); }
       | term
       ;

term   : term MULOP_MULTIPLY factor  { $$ = (long)malloc_op_node("*",
                         (struct node *)$1,
                         (struct node *)$3); }
       | factor
       ;

factor : PAREN_LEFT expr PAREN_RIGHT { $$ = $2; }
       | NUMBER                      { $$ = (long)malloc_number_node($1); }
       ;
*/
%%
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

  root_node->print_node(stdout, root_node);
  return result;
}

void yyerror(char const *s) {
  fprintf(stderr, "ERROR at line %d: %s\n", yylineno, s);
}
