#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <assert.h>
#include <errno.h>
#include <limits.h>
#include "lexer.h"
#include "y.tab.h"
#include "node.h"

/*This is a butchered version of "node" from the in-class example. 
  It stores the node values and other attributes to be set to 
  the yylval variable after calling yylex.
  These objects will likely be used later as parts of nodes.
*/

extern int yylineno;

/**
    node_create - create a generic node struct

    parameters: kind = value of kinds enum
                subkind = value of tokens, constant_types, resvwords, or ops enums

    returns: a pointer to the new generic node

    side effects: memory allocated on heap

**/

struct node *node_create(int kind, int subkind) {
  struct node *n;

  n = malloc(sizeof(struct node));
  assert(NULL != n);

  n->kind = kind;
  n->subkind = subkind;
  n->line_number = yylineno;
  return n;
}

void print_number(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_number\n"); 
#endif
    fprintf(output, "%lu",n->data.number.value);
}

/**
    node_number - create a number node struct

    parameters: value = numeric value of token
                subkind = CON_SHORT, CON_INT, CON_ULONG from constant_types

    returns: a pointer to the new number node

    side effects: memory allocated on heap in call to node_create

**/

struct node *node_number(long int value, int subkind) {
    struct node * nd = node_create(CONSTANT, subkind);
    nd->data.number.value = value;
    nd->print_node=print_number;
    return nd;
}

void print_identifier(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_identifier\n"); 
#endif
    fputs(n->data.identifier.name, output);
}

/**
    node_identifier - create an identifier node struct

    parameters: text = string representation of the identifier

    returns: a pointer to the new identifier node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_identifier(char *text) {
    struct node * nd = node_create(IDENTIFIER, ID);
    nd->data.identifier.name = (char *)malloc(31);
    strcpy(nd->data.identifier.name,text);
    nd->print_node=print_identifier;
    nd->line_number = 1;
#ifdef DEBUG
    printf("%s\n",text);
#endif
    return nd;

}

void print_string(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_string\n"); 
#endif
    fputs(n->data.string.value, output);
}

/**
    node_identifier - create a string node struct

    parameters: text = value of the string
                length = length of the string, stored for cases where null appears in the middle

    returns: a pointer to the new string node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_string(char *text, int length) {
    struct node * nd = node_create(CONSTANT, STRING);
    strcpy(nd->data.string.value,text);
    nd->data.string.length = length;
    nd->print_node = print_string;
    return nd;
}

void print_ternary_operation(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_ternary_operation\n"); 
#endif
    n->data.ternary_operation.left_operand->print_node(output, n->data.ternary_operation.left_operand);
    fputs("?", output);
    n->data.ternary_operation.middle_operand->print_node(output, n->data.ternary_operation.middle_operand);
    fputs(":", output);
    n->data.ternary_operation.right_operand->print_node(output, n->data.ternary_operation.right_operand);
}

struct node *node_ternary_operation(int operation1, int operation2, struct node *left_operand, struct node *middle_operand, struct node *right_operand)
{
  struct node *node = node_create(NODE_TERNARY_OPERATION, operation1);
  node->data.ternary_operation.operation1 = operation1;
  node->data.ternary_operation.operation2 = operation2;
  node->data.ternary_operation.left_operand = left_operand;
  node->data.ternary_operation.middle_operand = middle_operand;
  node->data.ternary_operation.right_operand = right_operand;
  node->data.ternary_operation.result = 0;
  return node;
}

void print_binary_op(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_binary_op\n"); 
#endif
    char * op;
    switch(n->data.binary_operation.operation) {
        case ASSIGNMENTADDITION:
            op = "+=";
        break;
        case ASSIGNMENTBITWISEAND:
            op = "&=";
        break;
        case ASSIGNMENTBITWISEOR:
            op = "|=";
        break;
        case ASSIGNMENTBITWISEXOR:
            op = "^=";
        break;
        case ASSIGNMENTDIVISION:
            op = "/=";
        break;
        case ASSIGNMENTLEFTSHIFT:
            op = "<<=";
        break;
        case ASSIGNMENTMULTIPLICATION:
            op = "*=";
        break;
        case ASSIGNMENTREMAINDER:
            op = "%=";
        break;
        case ASSIGNMENTRIGHTSHIFT:
            op = ">>=";
        break;
        case ASSIGNMENTSIMPLE:
            op = "=";
        break;
        case ASSIGNMENTSUBTRACTION:
            op = "-=";
        break;
        case ADDITION:
            op = "+";
        break;
        case SUBTRACTION:
            op = "-";
        break;
        case MULTIPLY:
            op = "*";
        break;
        case DIVIDE:
            op = "/";
        break;
        case REMAINDER:
            op = "/";
        break;
        case BITAND:
            op = "&";
        break;
        case BITOR:
            op = "|";
        break;
        case BITXOR:
            op = "^";
        break;
        case EQ:
            op = "==";
        break;
        case GE:
            op = ">=";
        break;
        case GT:
            op = ">";
        break;
        case LE:
            op = "<=";
        break;
        case LT:
            op = "<";
        break;
        case NE:
            op = "<";
        break;
        case LEFT:
            op = "<<";
        break;
        case RIGHT:
            op = ">>";
        break;
        default:
            printf("oops!!\n");
        break;
    }
    
    fputs("(", output);
    n->data.binary_operation.left_operand->print_node(output, n->data.binary_operation.left_operand);
    fputs(op,output);
    n->data.binary_operation.right_operand->print_node(output, n->data.binary_operation.right_operand);
    fputs(")", output);
}


struct node *node_binary_operation(int operation, struct node *left_operand,
                                   struct node *right_operand)
{
  struct node *node = node_create(NODE_BINARY_OPERATION, NODE_BINARY_OPERATION);
  node->data.binary_operation.operation = operation;
  node->data.binary_operation.left_operand = left_operand;
  node->data.binary_operation.right_operand = right_operand;
  node->data.binary_operation.result = 0;
  node->print_node=print_binary_op;
  return node;
}

void print_unary_op(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_unary_op\n"); 
#endif
    char * op;
    switch(n->data.unary_operation.operation) {
        case ADDITION:
            op = "+";
        break;
        case SUBTRACTION:
            op = "-";
        break;
        case MULTIPLY:
            op = "*";
        break;
        case BITAND:
            op = "&";
        break;
        case BITWISENEG:
            op = "~";
        break;
        case LOGNEG:
            op = "!";
        break;
        case INCDECINCREMENT:
            op = "++";
        break;
        case INCDECDECREMENT:
            op = "--";
        break;
        default:
            printf("I'm dumb\n");
        break;
    }
    
    fputs("(", output);
    fputs(op,output);
    n->data.unary_operation.right_operand->print_node(output, n->data.unary_operation.right_operand);
    fputs(")", output);
}

struct node *node_unary_operation(int operation, struct node *right_operand)
{
  struct node *node = node_create(NODE_UNARY_OPERATION, NODE_UNARY_OPERATION);
  node->data.unary_operation.operation = operation;
  node->data.unary_operation.right_operand = right_operand;
  node->data.unary_operation.result = 0;
  node->print_node=print_unary_op;
  return node;
}

struct node *node_expression_statement(struct node *expression)
{
  struct node *node = node_create(NODE_EXPRESSION_STATEMENT, NODE_EXPRESSION_STATEMENT);
  node->data.expression_statement.expression = expression;
  return node;
}

void print_statement_list(FILE *output, struct node*n){
  if (NULL != n->data.statement_list.init) {
#ifdef DEBUG
    printf("%d\n", n->data.statement_list.init->kind);
#endif
    n->data.statement_list.init->print_node(output, n->data.statement_list.init);
  }
#ifdef DEBUG
  printf("%d\n", n->data.statement_list.statement->kind);
#endif
  n->data.statement_list.statement->print_node(output, n->data.statement_list.statement);
  fputs("\n", output);
}

struct node *node_statement_list(struct node *init, struct node *statement) {
  struct node *node = node_create(NODE_STATEMENT_LIST, NODE_STATEMENT_LIST);
  node->data.statement_list.init = init;
  node->data.statement_list.statement = statement;
  node->print_node = print_statement_list;
  return node;
}

void print_comma_expr(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_comma_expr\n");
#endif
    if (n->data.comma_expr.init != NULL) {
        n->data.comma_expr.init->print_node(output, n->data.comma_expr.init);
    }
    fputs(",", output);
    n->data.comma_expr.expr->print_node(output, n->data.comma_expr.expr);
}

struct node *node_comma_expr(struct node *init, struct node *expr) {
  struct node *node = node_create(NODE_COMMA_EXPR, NODE_COMMA_EXPR);
  node->data.comma_expr.init = init;
  node->data.comma_expr.expr = expr;
  return node;
}

void print_while(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_while\n");
#endif
    if (n->subkind == WHILE) {
        fputs("while", output);
        n->data.while_statement.expr->print_node(output, n->data.while_statement.expr);
    } else if (n->subkind == DO_WHILE) {
        fputs("do", output);
    }
    n->data.while_statement.statement->print_node(output, n->data.while_statement.statement);
    if (n->subkind == DO_WHILE) {
        fputs("while ", output);    
        n->data.while_statement.expr->print_node(output, n->data.while_statement.expr);
        fputs(";", output);    
    }
}

struct node *node_while_statement(int while_type, struct node *expr, struct node *statement) {
    struct node * node = node_create(NODE_WHILE_STATEMENT, while_type);
    node->data.while_statement.expr = expr;
    node->data.while_statement.statement = statement;
    node->print_node = print_while;
    return node;
}

void print_for(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_for\n");
#endif
    fputs("for (", output);
    n->data.for_statement.initial_clause->print_node(output, n->data.for_statement.initial_clause);
    fputs(" ; ", output);
    n->data.for_statement.middle_expr->print_node(output, n->data.for_statement.middle_expr);
    fputs(" ; ", output);
    n->data.for_statement.end_expr->print_node(output, n->data.for_statement.end_expr);
    fputs(")", output);
    n->data.for_statement.statement->print_node(output, n->data.for_statement.statement);
}

struct node *node_for_statement(struct node *initial_clause, struct node *middle_expr, struct node* end_expr, struct node *statement) {
    struct node * node = node_create(NODE_FOR_STATEMENT, NODE_FOR_STATEMENT);
    node->data.for_statement.initial_clause = initial_clause;
    node->data.for_statement.middle_expr = middle_expr;
    node->data.for_statement.end_expr = end_expr;
    node->data.for_statement.statement = statement;
    node->print_node = print_for;
    return node;
}

void print_if_else(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_if_else\n");
#endif
    fputs("if ", output);
    n->data.if_else_statement.expr->print_node(output, n->data.if_else_statement.expr);
    n->data.if_else_statement.statement_if->print_node(output, n->data.if_else_statement.statement_if);
    fputs("else ", output);
    n->data.if_else_statement.statement_else->print_node(output, n->data.if_else_statement.statement_else);
    
}

struct node *node_if_else_statement(struct node *expr, struct node *statement_if, struct node *statement_else) {
    struct node * node = node_create(NODE_IF_ELSE_STATEMENT, NODE_IF_ELSE_STATEMENT);
    node->data.if_else_statement.expr = expr;
    node->data.if_else_statement.statement_if = statement_if;
    node->data.if_else_statement.statement_else = statement_else;
    node->print_node = print_if_else;
    return node;
}

void print_if(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_if\n");
#endif
    fputs("if ", output);
    n->data.if_statement.expr->print_node(output, n->data.if_statement.expr);
    n->data.if_statement.statement_if->print_node(output, n->data.if_statement.statement_if);
}

struct node *node_if_statement(struct node *expr, struct node *statement_if) {
    struct node * node = node_create(NODE_IF_STATEMENT, NODE_IF_STATEMENT);
    node->data.if_statement.expr = expr;
    node->data.if_statement.statement_if = statement_if;
    node->print_node = print_if;
    return node;
}

struct node *node_subscript_statement(struct node *postfix_expr, struct node *expr) {
    struct node * node = node_create(NODE_SUBSCRIPT_STATEMENT, NODE_SUBSCRIPT_STATEMENT);
    node->data.subscript_statement.postfix_expr = postfix_expr;
    node->data.subscript_statement.expr = expr;
    return node;
}

void print_node_break(FILE * output, struct node * n) {
    fputs("break;\n", output);
}

struct node *node_break() {
    struct node * node = node_create(NODE_BREAK, NODE_BREAK);
    node->print_node=print_node_break;
    return node;
}

void print_node_continue(FILE * output, struct node * n) {
    fputs("continue;\n", output);
}

struct node *node_continue() {
    struct node * node = node_create(NODE_CONTINUE, NODE_CONTINUE);
    node->print_node=print_node_continue;
    return node;
}

void print_node_return(FILE * output, struct node * n) {
    fputs("return ", output);
    n->data.return_stmt.id->print_node(output, n->data.return_stmt.id);
    fputs(";\n", output);
}

struct node *node_return(struct node* id) {
    struct node * node = node_create(NODE_RETURN, NODE_RETURN);
    node->data.return_stmt.id = id;
    node->print_node=print_node_return;
    return node;
}

void print_node_goto(FILE * output, struct node * n) {
    fputs("goto ", output);
    n->data.return_stmt.id->print_node(output, n->data.return_stmt.id);
    fputs(";\n", output);
}

struct node *node_goto(struct node* id) {
    struct node * node = node_create(NODE_GOTO, NODE_GOTO);
    node->data.goto_stmt.id = id;
    node->print_node=print_node_goto;
    return node;
}

void print_type(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_type\n"); 
#endif
    int i;
    char * type_str[] = {
        "void",
        "signed char",
        "unsigned char",
        "signed short",
        "unsigned short",
        "signed int",
        "unsigned int",
        "signed long",
        "unsigned long"
        };
    
    fputs(type_str[n->subkind], output);

    fputs(" ", output);

    for (i = 0; i < n->data.type.pointer_depth; i++) {
        fputs("(*",output);
    }
    if (n->data.type.id != NULL) {
        n->data.type.id->print_node(output, n->data.type.id);
        /*fputs(n->data.type.id->data.identifier.name, output);*/
    }
    for (i = 0; i < n->data.type.pointer_depth; i++) {
        fputs(")", output);
    }
}

struct node *node_type(int type, int pointer_depth, struct node* id) {
    struct node * node = node_create(NODE_TYPE, type);
    node->data.type.pointer_depth = pointer_depth;
    node->data.type.id = id;
    node->print_node = print_type;
    return node;
}

struct node *node_function_call(struct node* postfix_expr, struct node* expr_list) {
    struct node * node = node_create(NODE_FUNCTION_CALL, NODE_FUNCTION_CALL);
    node->data.function_call.postfix_expr = postfix_expr;
    node->data.function_call.expr_list = expr_list;
    return node;
    
}

struct node *node_cast_expr(struct node* type_expr, struct node* expr) {
    struct node * node = node_create(NODE_CAST_EXPR, NODE_CAST_EXPR);
    node->data.cast_expr.type_expr = type_expr;
    node->data.cast_expr.expr = expr;
    return node;

}

void print_func_decl(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_func_decl\n"); 
#endif
    /*n->data.func_declarator.decl->print_node(output, n->data.func_declarator.decl);*/
    fputs("(",output);
    n->data.func_declarator.param_list->print_node(output, n->data.func_declarator.param_list);
    fputs(")",output);
}


struct node *node_func_declarator(struct node *decl, struct node *param_list) {
    struct node * node = node_create(NODE_FUNCTION_DECL, NODE_FUNCTION_DECL);
    node->data.func_declarator.decl = decl;
    node->data.func_declarator.param_list = param_list;
    node->print_node=print_func_decl;
    return node;
}

void print_func_def(FILE * output, struct node * n) {
    n->data.func_def.func_spec->print_node(output, n->data.func_def.func_spec);
    n->data.func_def.stmt->print_node(output, n->data.func_def.stmt);
}

struct node *node_function_def(struct node* func_spec, struct node* stmt) {
    struct node * node = node_create(NODE_FUNCTION_DEF, NODE_FUNCTION_DEF);
    node->data.func_def.func_spec = func_spec;
    node->data.func_def.stmt = stmt;
    node->print_node=print_func_def;
    return node;
}

void print_func_def_spec(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_func_def_spec\n"); 
#endif
    n->data.func_def_spec.spec->print_node(output, n->data.func_def_spec.spec);
    n->data.func_def_spec.decl->print_node(output, n->data.func_def_spec.decl);
}

struct node *node_function_def_spec(struct node* spec, struct node* decl) {
    struct node * node = node_create(NODE_FUNCTION_DEF_SPEC, NODE_FUNCTION_DEF_SPEC);
    node->data.func_def_spec.spec = spec;
    node->data.func_def_spec.decl = decl;
    node->print_node=print_func_def_spec;
    return node;
}


struct node *node_labeled_statement(struct node *label, struct node *statement) {
    struct node * node = node_create(NODE_LABELED_STMT, NODE_LABELED_STMT);
    node->data.lbl_stmt.label = label;
    node->data.lbl_stmt.statement = statement;
    return node;
}

void print_param_list(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_param_list\n"); 
#endif
  if (NULL != n->data.param_list.init) {
    n->data.param_list.init->print_node(output, n->data.param_list.init);
  }
  n->data.param_list.param->print_node(output, n->data.param_list.param);
}

struct node *node_param_list(struct node *init, struct node *param){
    struct node * node = node_create(NODE_PARAM_LIST, NODE_PARAM_LIST);
    node->data.param_list.init = init;
    node->data.param_list.param = param;
    node->print_node=print_param_list;
    return node;
}

void print_node_decl(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_node_decl\n");
#endif
    n->data.decl.decl_spec->print_node(output, n->data.decl.decl_spec);
    n->data.decl.decl_list->print_node(output, n->data.decl.decl_list);
}

struct node *node_decl(struct node * decl_spec, struct node * decl_list) {
    struct node * node = node_create(NODE_DECL, NODE_DECL);
    node->data.decl.decl_spec = decl_spec;
    node->data.decl.decl_list = decl_list;
    node->print_node=print_node_decl;
    return node;
}

void print_node_decl_list(FILE * output, struct node * n) {
#ifdef DEBUG
    printf("print_node_decl_list\n"); 
#endif
  if (NULL != n->data.decl_list.init) {
    n->data.decl_list.init->print_node(output, n->data.decl_list.init);
  }
  n->data.decl_list.decl->print_node(output, n->data.decl_list.decl);
}

struct node *node_decl_list(struct node * init, struct node * decl) {
    struct node * node = node_create(NODE_DECL_LIST, NODE_DECL_LIST);
    node->data.decl_list.init = init;
    node->data.decl_list.decl = decl;
    node->print_node=print_node_decl_list;
    return node;
}



long int node_get_result(struct node *expression) {
  switch (expression->kind) {
    case NODE_NUMBER:
      return expression->data.number.value;
    case NODE_IDENTIFIER:
      return expression->data.identifier.value;
    case NODE_BINARY_OPERATION:
      return expression->data.binary_operation.result;
    default:
      assert(0);
      return 0;
  }
}

/***************************************
 * PARSE TREE PRETTY PRINTER FUNCTIONS *
 ***************************************/

void node_print_expression(FILE *output, struct node *expression);

void node_print_binary_operation(FILE *output, struct node *binary_operation) {
  static const char *binary_operators[] = {
    "*",    /*  0 = BINOP_MULTIPLICATION */
    "/",    /*  1 = BINOP_DIVISION */
    "+",    /*  2 = BINOP_ADDITION */
    "-",    /*  3 = BINOP_SUBTRACTION */
    "=",    /*  4 = BINOP_ASSIGN */
    NULL
  };

  assert(NULL != binary_operation && NODE_BINARY_OPERATION == binary_operation->kind);

  fputs("(", output);
  node_print_expression(output, binary_operation->data.binary_operation.left_operand);
  fputs(" ", output);
  fputs(binary_operators[binary_operation->data.binary_operation.operation], output);
  fputs(" ", output);
  node_print_expression(output, binary_operation->data.binary_operation.right_operand);
  fputs(")", output);
}

void node_print_number(FILE *output, struct node *number) {
  assert(NULL != number);
  assert(NODE_NUMBER == number->kind);
}

/*
 * After the symbol table pass, we can print out the symbol address
 * for each identifier, so that we can compare instances of the same
 * variable and ensure that they have the same symbol.
 */
void node_print_identifier(FILE *output, struct node *identifier) {
  assert(NULL != identifier);
  assert(NODE_IDENTIFIER == identifier->kind);
  fputs(identifier->data.identifier.name, output);
  fprintf(output, "$%lu", (unsigned long)identifier->data.identifier.value);
}

void node_print_expression(FILE *output, struct node *expression) {
  assert(NULL != expression);
  switch (expression->kind) {
    /*case NODE_UNARY_OPERATION:
      node_print_unary_operation(output, expression);
      break;*/
    case NODE_BINARY_OPERATION:
      node_print_binary_operation(output, expression);
      break;
    /*case NODE_TERNARY_OPERATION:
      node_print_ternary_operation(output, expression);
      break;*/
    case NODE_IDENTIFIER:
      node_print_identifier(output, expression);
      break;
    case NODE_NUMBER:
      node_print_number(output, expression);
      break;
    default:
      assert(0);
      break;
  }
}

void node_print_expression_statement(FILE *output, struct node *expression_statement) {
  assert(NULL != expression_statement);
  assert(NODE_EXPRESSION_STATEMENT == expression_statement->kind);

  node_print_expression(output, expression_statement->data.expression_statement.expression);

}

void node_print_statement_list(FILE *output, struct node *statement_list) {
  assert(NODE_STATEMENT_LIST == statement_list->kind);

  if (NULL != statement_list->data.statement_list.init) {
    node_print_statement_list(output, statement_list->data.statement_list.init);
  }
  node_print_expression_statement(output, statement_list->data.statement_list.statement);
  fputs(";\n", output);
}

