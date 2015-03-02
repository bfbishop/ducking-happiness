%{

#define YYSTYPE struct node *
#define YYERROR_VERBOSE
#include <stdio.h>
#include "node.h"

void print_tree(struct node *node_p);
int yylex(void);
int main(void);
void yyerror(char const *s);
struct node * root_node;

/*Set to 1 in the event of a recoverable error*/
int recoverable_error = 0;

%}

%start program

%token RW_BREAK RW_CHAR RW_CONTINUE RW_DO RW_ELSE RW_FOR RW_GOTO RW_IF RW_INT RW_LONG RW_RETURN RW_SHORT RW_SIGNED RW_UNSIGNED RW_VOID RW_WHILE

%nonassoc PARENRIGHT
%nonassoc RW_ELSE

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
%token PARENRIGHT
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

abstract_declarator :  pointer { $$ = node_abstract_decl((long int)$1, NULL); }
    |	 direct_abstract_declarator { $$ = node_abstract_decl(0, $1); }
	|	 pointer direct_abstract_declarator { $$ = node_abstract_decl((long int)$1, $2); }
;
additive_expr :  multiplicative_expr 
	|	 additive_expr add_op multiplicative_expr { $$ = node_binary_operation((long int)$2, $1, $3); }
;
add_op :  ADDITION { $$ = (struct node *)ADDITION; }
	|	 SUBTRACTION { $$ = (struct node *)SUBTRACTION; }
;
address_expr :  BITAND cast_expr { $$ = node_unary_operation(BITAND, PRE, $2); } 
;
array_declarator :  direct_declarator BRACKETLEFT BRACKETRIGHT { $$ = node_subscript_decl($1, NULL); }
    |    direct_declarator BRACKETLEFT constant_expr BRACKETRIGHT  { $$ = node_subscript_decl($1, $3); } 
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
	|	 bitwise_and_expr BITAND equality_expr { $$ = node_binary_operation(BITAND, $1, $3); }
;

bitwise_negation_expr :  BITWISENEG cast_expr { $$ = node_unary_operation(BITWISENEG, PRE, $2); }
;
bitwise_or_expr :  bitwise_xor_expr 
	|	 bitwise_or_expr BITOR bitwise_xor_expr { $$ = node_binary_operation(BITOR, $1, $3); }
;
bitwise_xor_expr :  bitwise_and_expr 
	|	 bitwise_xor_expr BITXOR bitwise_and_expr { $$ = node_binary_operation(BITXOR, $1, $3); }
;
break_statement :  RW_BREAK SEPSEMICOLON { $$ = node_break(); }
;
cast_expr :  unary_expr 
	|	 PARENLEFT type_name PARENRIGHT cast_expr { $$ = node_cast_expr($2, $4); }
;
character_type_specifier :  RW_CHAR { $$ = node_type_spec(TYPE_SCHAR);}
	|	 RW_SIGNED RW_CHAR { $$ = node_type_spec(TYPE_SCHAR);}
	|	 RW_UNSIGNED RW_CHAR { $$ = node_type_spec(TYPE_UCHAR);}
;
comma_expr :  assignment_expr 
	|	 comma_expr SEQUENTIALCOMMA assignment_expr { $$ = node_comma_expr($2, $1);}
;
compound_statement :  BRACELEFT BRACERIGHT { $$ = node_compound_statement(NULL); }
    |    BRACELEFT declaration_or_statement_list BRACERIGHT { $$ = node_compound_statement($2); }
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
	|	 BRACKETLEFT BRACKETRIGHT { $$ = node_subscript_decl(NULL, NULL); }
	|	 BRACKETLEFT constant_expr BRACKETRIGHT { $$ = node_subscript_decl(NULL, $3); }
	|	 direct_abstract_declarator BRACKETLEFT BRACKETRIGHT { $$ = node_subscript_decl($1, NULL); }
	|	 direct_abstract_declarator BRACKETLEFT constant_expr BRACKETRIGHT { $$ = node_subscript_decl($1, $3); }
;
direct_declarator :  simple_declarator
	|	 PARENLEFT declarator PARENRIGHT { $$ = $2; }
	|	 function_declarator  
	|	 array_declarator 
;
do_statement :  RW_DO statement RW_WHILE PARENLEFT expr PARENRIGHT SEPSEMICOLON { $$ = node_while_statement(DO_WHILE, $5, $2); }
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
	|	 expression_list SEQUENTIALCOMMA assignment_expr { $$ = node_expr_list($1, $3); }
;
expression_statement :  expr SEPSEMICOLON { $$ = node_expr_statement($1); }
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
    |   postfix_expr PARENLEFT expression_list PARENRIGHT { $$ = node_function_call($1, $3); }
;

function_declarator :  direct_declarator PARENLEFT parameter_type_list PARENRIGHT { 
    $$ = node_func_declarator($1, $3); }
;

function_definition :  function_def_specifier compound_statement { $$ = node_function_def($1, $2); }
;
/*Per class site, functions need return types*/
function_def_specifier :  /*declarator { $$ = node_function_def_spec(NULL, $1); }
    | */  declaration_specifiers declarator { $$ = node_function_def_spec($1, $2); }
;

goto_statement :  RW_GOTO named_label SEPSEMICOLON { $$ = node_goto($2); }
;
if_else_statement :  RW_IF PARENLEFT expr PARENRIGHT statement RW_ELSE statement { $$ = node_if_else_statement($3, $5, $7); }
;
if_statement :  RW_IF PARENLEFT expr PARENRIGHT statement
{ $$ = node_if_statement($3, $5); }
;
indirection_expr : MULTIPLY cast_expr { $$ = node_unary_operation(MULTIPLY, PRE, $2); }
;
initial_clause :  expr 
;
initialized_declarator :  declarator 
;
initialized_declarator_list :  initialized_declarator { $$ = node_decl_list(NULL, $1); }
|	 initialized_declarator_list SEQUENTIALCOMMA initialized_declarator { $$ = node_decl_list($1, $3); }
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
logical_negation_expr :  LOGNEG cast_expr { $$ = node_unary_operation(LOGNEG, PRE, $2); }
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
null_statement :  SEPSEMICOLON { $$ = node_null_statement(); }
;
parameter_decl :  declaration_specifiers declarator { $$ = node_param_decl($1,$2); }
|	 declaration_specifiers { $$ = node_param_decl($1,NULL); }
|	 declaration_specifiers abstract_declarator { $$ = node_param_decl($1,$2); }
;
parameter_list :  parameter_decl { $$ = node_param_list(NULL, $1); }
|	 parameter_list SEQUENTIALCOMMA parameter_decl { $$ = node_param_list($1, $3); }
;
parameter_type_list :  parameter_list 
;
parenthesized_expr :  PARENLEFT expr PARENRIGHT { $$ = $2; }
;
/* MULTIPLY == * */
pointer :  MULTIPLY { $$ = (struct node *)1; }
|	 MULTIPLY pointer { $$ = (struct node *)((long int) $2+1); }
;
pointer_declarator :  pointer direct_declarator   { $$ = node_pointer_decl((long int)$1, $2);
                                                  }
;
postdecrement_expr :  postfix_expr INCDECDECREMENT { $$ = node_unary_operation(INCDECDECREMENT, POST, $1); }
;
postfix_expr :  primary_expr 
	|	 subscript_expr 
    |    function_call 
	|	 postincrement_expr 
	|	 postdecrement_expr 
;
postincrement_expr :  postfix_expr INCDECINCREMENT { $$ = node_unary_operation(INCDECINCREMENT, POST, $1); }
;
predecrement_expr :  INCDECDECREMENT unary_expr { $$ = node_unary_operation(INCDECDECREMENT, PRE, $2); }
;
preincrement_expr :  INCDECINCREMENT unary_expr { $$ = node_unary_operation(INCDECINCREMENT, PRE, $2); } 
;
primary_expr :  ID { $$ = node_primary_expr($1); }
	|	 constant { $$ = node_primary_expr($1); }
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
signed_type_specifier :  RW_SHORT { $$ = node_type_spec(TYPE_SSHORT);} 
	|	 RW_SHORT RW_INT { $$ = node_type_spec(TYPE_SSHORT);} 
	|	 RW_SIGNED RW_SHORT { $$ = node_type_spec(TYPE_SSHORT);}
	|	 RW_SIGNED RW_SHORT RW_INT { $$ = node_type_spec(TYPE_SSHORT);}
	|	 RW_INT { $$ = node_type_spec(TYPE_SINT);}
	|	 RW_SIGNED RW_INT { $$ = node_type_spec(TYPE_SINT);}
	|	 RW_SIGNED { $$ = node_type_spec(TYPE_SINT);}
	|	 RW_LONG { $$ = node_type_spec(TYPE_SLONG);}
	|	 RW_LONG RW_INT { $$ = node_type_spec(TYPE_SLONG);}
	|	 RW_SIGNED RW_LONG { $$ = node_type_spec(TYPE_SLONG);}
	|	 RW_SIGNED RW_LONG RW_INT { $$ = node_type_spec(TYPE_SLONG);}
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
    | error { recoverable_error = 1; yyerrok ; } 
;
/* a[b] == *(a+b) here */
subscript_expr : postfix_expr BRACKETLEFT expr BRACKETRIGHT 
    { struct node * sum = node_binary_operation(ADDITION, $1, $3); 
      $$ = node_unary_operation(MULTIPLY, PRE, sum); 
    }
;
top_level_decl : decl
    |   function_definition 
;
translation_unit :  top_level_decl { $$ = node_translation_unit(NULL, $1) ; }
	|	 translation_unit top_level_decl { $$ = node_translation_unit($1, $2); }
;
type_name :  declaration_specifiers { $$ = node_type_name($1, NULL);  }
    |   declaration_specifiers abstract_declarator { $$ = node_type_name($1, $2); }
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
unary_minus_expr : SUBTRACTION cast_expr { $$ = node_unary_operation(SUBTRACTION, PRE, $2);}
;
unary_plus_expr : ADDITION cast_expr { $$ = node_unary_operation(ADDITION, PRE, $2);}
;
unsigned_type_specifier :  
         RW_UNSIGNED RW_SHORT RW_INT { $$ = node_type_spec(TYPE_USHORT);}
    |    RW_UNSIGNED RW_SHORT { $$ = node_type_spec(TYPE_USHORT);}
	|	 RW_UNSIGNED RW_INT { $$ = node_type_spec(TYPE_UINT);}
	|	 RW_UNSIGNED { $$ = node_type_spec(TYPE_UINT);}
	|	 RW_UNSIGNED RW_LONG RW_INT { $$ = node_type_spec(TYPE_ULONG);}
	|	 RW_UNSIGNED RW_LONG { $$ = node_type_spec(TYPE_ULONG);}
;
void_type_specifier :  RW_VOID { $$ = node_type_spec(TYPE_VOID); }
;
while_statement :  RW_WHILE PARENLEFT expr PARENRIGHT statement { $$ = node_while_statement(WHILE, $3, $5); }
;


%%
#include <stdio.h>
#include <stdlib.h>
#include "lex.yy.c"
#include "node.h"

int main(void) {
  int result;
  result = yyparse();
  if (!result && !recoverable_error) {
      root_node->print_node(stdout, root_node, 0);
  }
  return result;
}

void yyerror(char const *s) {
  fprintf(stderr, "ERROR at line %d: %s\n", yylineno, s);
}

