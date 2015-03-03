#ifndef _NODE_H
#define _NODE_H

#include <stdio.h>
#include <stdbool.h>

/*This is a butchered version of "node" from the in-class example. 
  It stores the node values and other attributes to be set to 
  the yylval variable after calling yylex.
  These objects will likely be used later as parts of nodes.
*/

enum node_kinds {
        NODE_NUMBER,
        NODE_IDENTIFIER,
        NODE_BINARY_OPERATION,
        NODE_EXPRESSION_STATEMENT,
        NODE_STATEMENT_LIST,
        NODE_UNARY_OPERATION,
        NODE_TERNARY_OPERATION,
        NODE_WHILE_STATEMENT,
        NODE_FOR_STATEMENT,
        NODE_IF_ELSE_STATEMENT,
        NODE_IF_STATEMENT,
        NODE_SUBSCRIPT_STATEMENT,
        NODE_RETURN,
        NODE_BREAK,
        NODE_CONTINUE,
        NODE_GOTO,
        NODE_TYPE,
        NODE_FUNCTION_CALL,
        NODE_FUNCTION_DECL,
        NODE_FUNCTION_DEF,
        NODE_FUNCTION_DEF_SPEC,
        NODE_CAST_EXPR,
        NODE_COMMA_EXPR,
        NODE_PRIM_EXPR,
        NODE_LABELED_STMT,
        NODE_PARAM_DECL,
        NODE_PARAM_LIST,
        NODE_DECL,
        NODE_DECL_LIST,
        NODE_DECLARATOR,
        NODE_POINTER_DECL,
        NODE_COMPOUND_STATEMENT,
        NODE_EXPR_STATEMENT,
        NODE_NULL_STATEMENT,
        NODE_TYPE_NAME,
        NODE_ABS_DECL
    };

enum while_kinds {
        DO_WHILE,
        WHILE
    };

enum unary_kinds {
        PRE,
        POST
    };

enum base_types {
        TYPE_VOID,
        TYPE_SCHAR,
        TYPE_UCHAR,
        TYPE_SSHORT,
        TYPE_USHORT,
        TYPE_SINT,
        TYPE_UINT,
        TYPE_SLONG,
        TYPE_ULONG
    };

struct node {
  int kind; /*kinds enum*/
  int subkind; /*resvwords, ops, or tokens enum*/
  int line_number;
  void (*print_node)(FILE *, struct node*, int depth);
  union {
    struct {
      unsigned long value;
    } number;
    struct {
      char * name;
      long int value;
    } identifier;
    struct {
      char * value;
      int length;
    } string;
    struct {
      int operation1;
      int operation2;
      struct node *left_operand;
      struct node *middle_operand;
      struct node *right_operand;
      long int result;
    } ternary_operation;
    struct {
      int operation;
      struct node *left_operand;
      struct node *right_operand;
      long int result;
    } binary_operation;
    struct {
      int operation;
      int pre_post;
      struct node *right_operand;
      long int result;
    } unary_operation;
    struct {
      struct node *init;
      struct node *statement;
    } statement_list;
    struct {
      struct node *expr;
      struct node *statement;
    } while_statement;
    struct {
      struct node *initial_clause;
      struct node *middle_expr;
      struct node *end_expr;
      struct node *statement;
    } for_statement;
    struct {
      struct node *expr;
      struct node *statement_if;
      struct node *statement_else;
    } if_else_statement;
    struct {
      struct node *expr;
      struct node *statement_if;
    } if_statement;
    struct {
      struct node *decl;
      struct node *const_expr;
    } subscript_decl;
    struct {
      struct node * id;
    } return_stmt;
    struct {
      struct node * id;
    } goto_stmt;
    struct {
      int type;
    } type_spec;
    struct {
      struct node * postfix_expr;
      struct node * expr_list;
    } function_call;
    struct {
      struct node * type_expr;
      struct node * expr;
    } cast_expr;
    struct {
      struct node *init;
      struct node *expr;
    } comma_expr;
    struct {
      struct node *expr;
    } prim_expr;
    struct {
      struct node *decl;
      struct node *param_list;
    } func_declarator;
    struct {
      struct node *label;
      struct node *statement;
    } lbl_stmt;
    struct {
      struct node *func_spec;
      struct node *stmt;
    } func_def;
    struct {
      struct node *spec;
      struct node *decl;
    } func_def_spec;
    struct {
      struct node *decl_spec;
      struct node *declrtr;
    } param_decl;
    struct {
      struct node *init;
      struct node *param;
    } param_list;
    struct {
      struct node *decl_spec;
      struct node *decl_list;
    } decl;
    struct {
      struct node *init;
      struct node *decl;
    } decl_list;
    struct {
      int depth;
      struct node *decl;
    } pointer_decl;
    struct {
      struct node *decl_or_stmt_list;
    } comp_stmt;
    struct {
      struct node *expr;
    } expr_stmt;
    struct {
      struct node *decl_spec;
      struct node *abs_decl;
    } type_name;
    struct {
      int pointer_depth;
      struct node *dir_abs_decl;
    } abs_decl;
  } data;
};

/* Constructors */
struct node *node_number(long int value, int subkind);
struct node *node_identifier(char *text);
struct node *node_string(char *text, int length);
struct node *node_statement_list(struct node *list, struct node *item);
struct node *node_ternary_operation(int operation1, int operation2, struct node *left_operand, struct node *middle_operand, struct node *right_operand);
struct node *node_binary_operation(int operation, struct node *left_operand,
                                   struct node *right_operand);
struct node *node_unary_operation(int operation, int pre_post, struct node *right_operand);
struct node *node_expression_statement(struct node *expression);
struct node *node_statement_list(struct node *init, struct node *statement);
struct node *node_while_statement(int while_type, struct node *expr, struct node *statement);
struct node *node_for_statement(struct node *initial_clause, struct node *middle_expr, struct node* end_expr, struct node *statement);
struct node *node_if_else_statement(struct node *expr, struct node *statement_if, struct node *statement_else);
struct node *node_if_statement(struct node *expr, struct node *statement_if);
struct node *node_subscript_decl(struct node *decl, struct node *const_expr);
struct node *node_return(struct node* id);
struct node *node_goto(struct node* id);
struct node *node_break();
struct node *node_continue();
struct node *node_type_spec(int type);
struct node *node_function_call(struct node* postfix_expr, struct node* expr_list);
struct node *node_function_declarator(struct node* postfix_expr, struct node* expr_list);
struct node *node_function_def(struct node* func_spec, struct node* stmt);
struct node *node_function_def_spec(struct node* spec, struct node* decl);
struct node *node_cast_expr(struct node* type_expr, struct node* expr);
struct node *node_comma_expr(struct node *init, struct node *expr);
struct node *node_primary_expr(struct node *expr);
struct node *node_func_declarator(struct node *decl, struct node *param_list);
struct node *node_labeled_statement(struct node *label, struct node *statement);
struct node *node_param_decl(struct node *decl_spec, struct node *declrtr);
struct node *node_param_list(struct node *init, struct node *param);
struct node *node_decl(struct node * decl_spec, struct node * decl_list);
struct node *node_decl_list(struct node * init, struct node * decl);
struct node *node_pointer_decl(int depth, struct node * decl);
struct node *node_compound_statement(struct node *decl_or_stmt_list);
struct node *node_expr_statement(struct node *expr);
struct node *node_null_statement();
struct node *node_type_name(struct node * decl_spec, struct node * abs_decl);
struct node *node_abstract_decl(long int pointer_depth, struct node * dir_abs_decl);

long int node_get_result(struct node *expression);

void node_print_statement_list(FILE *output, struct node *statement_list);


#endif
