#ifndef _SYMBOL_H
#define _SYMBOL_H
#include"node.h"

enum type_tree_kinds {
    ARRAY,
    POINTER,
    FUNCTION,
    BASE
};

enum scope_tree_kinds {
    TYPE,
    SCOPE
};

struct scope_tree_node {
    int kind; /*scope_tree_kinds enum - TYPE or SCOPE*/
    int type_kind; /*type_tree_kinds enum*/
    union {
        struct {
            int base_type; /*base_types enum from node.h*/
        } base;
        struct {
            struct scope_tree_node * declarator;
            struct scope_tree_node * const_expr;
        } array;
        struct {
            int pointer_depth;
            struct scope_tree_node * declarator;
        } pointer;
        struct {
            struct scope_tree_node * return_type;
            struct scope_tree_node * param_list;
        } function;
        struct {
            struct scope_tree_node * parent_scope;
            void * other_names_table;
            void * statement_label_table;
        } scope;
    } data_types;
};

/*Constant structs for base types. Using only one node for each so that comparison is easy*/
struct scope_tree_node unsigned_char;
struct scope_tree_node signed_char;
struct scope_tree_node unsigned_short;
struct scope_tree_node signed_short;
struct scope_tree_node unsigned_int;
struct scope_tree_node signed_int;
struct scope_tree_node unsigned_long;
struct scope_tree_node signed_long;
struct scope_tree_node _void;

struct scope_tree_node * type_node_create(int scope_tree_kind, int type_kind);
struct scope_tree_node * traverse_number(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_identifier(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_string(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_ternary_operation(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_binary_operation(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_unary_operation(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_expr_list(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_statement_list(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_while_statement(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_for_statement(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_if_else_statement(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_if_statement(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_subscript_decl(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_return_stmt(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_goto_stmt(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_type_spec(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_function_call(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_cast_expr(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_comma_expr(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_prim_expr(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_func_declarator(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_lbl_stmt(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_func_def(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_func_def_spec(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_param_decl(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_param_list(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_decl(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_decl_list(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_pointer_decl(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_comp_stmt(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_expr_stmt(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_type_name(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_abs_decl(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_translation_unit(struct node * n, struct scope_tree_node * parent_scope); 
struct scope_tree_node * traverse_null_statement(struct node * n, struct scope_tree_node * parent_scope);
struct scope_tree_node * traverse_break_statement(struct node * n, struct scope_tree_node * parent_scope);
struct scope_tree_node * traverse_continue_statement(struct node * n, struct scope_tree_node * parent_scope);
#endif
