#include"node.h"

struct type_node {
    int kind /*node_kinds enum*/
};

void traverse_number(struct node * n); 
void traverse_identifier(struct node * n); 
void traverse_string(struct node * n); 
void traverse_ternary_operation(struct node * n); 
void traverse_binary_operation(struct node * n); 
void traverse_unary_operation(struct node * n); 
void traverse_expr_list(struct node * n); 
void traverse_statement_list(struct node * n); 
void traverse_while_statement(struct node * n); 
void traverse_for_statement(struct node * n); 
void traverse_if_else_statement(struct node * n); 
void traverse_if_statement(struct node * n); 
void traverse_subscript_decl(struct node * n); 
void traverse_return_stmt(struct node * n); 
void traverse_goto_stmt(struct node * n); 
void traverse_type_spec(struct node * n); 
void traverse_function_call(struct node * n); 
void traverse_cast_expr(struct node * n); 
void traverse_comma_expr(struct node * n); 
void traverse_prim_expr(struct node * n); 
void traverse_func_declarator(struct node * n); 
void traverse_lbl_stmt(struct node * n); 
void traverse_func_def(struct node * n); 
void traverse_func_def_spec(struct node * n); 
void traverse_param_decl(struct node * n); 
void traverse_param_list(struct node * n); 
void traverse_decl(struct node * n); 
void traverse_decl_list(struct node * n); 
void traverse_pointer_decl(struct node * n); 
void traverse_comp_stmt(struct node * n); 
void traverse_expr_stmt(struct node * n); 
void traverse_type_name(struct node * n); 
void traverse_abs_decl(struct node * n); 
void traverse_translation_unit(struct node * n); 
void traverse_null_statement(struct node * n);
void traverse_break_statement(struct node * n);
void traverse_continue_statement(struct node * n);
