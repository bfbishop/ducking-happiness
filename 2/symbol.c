#include "node.h"
#include "symbol.h"


struct type_node * type_node_create(int parse_node_kind) {
    struct type_node * t;
    t = malloc(sizeof(struct type_node));
    t->kind = parse_node_kind;
}

int lookup_id(char * id) {
    return 9;
}

int lookup_labal(char * id) {
    return 9;
}

void traverse_number(struct node * n) { 
  return;
}

void traverse_identifier(struct node * n) { 
    lookup_id(n->data.identifier.name);
}

void traverse_string(struct node * n) { 
  return;
}

void traverse_ternary_operation(struct node * n) { 
  n->data.ternary_operation.left_operand->traverse_node(n->data.ternary_operation.left_operand);
  n->data.ternary_operation.middle_operand->traverse_node(n->data.ternary_operation.middle_operand);
  n->data.ternary_operation.right_operand->traverse_node(n->data.ternary_operation.right_operand);
}

void traverse_binary_operation(struct node * n) { 
  n->data.binary_operation.left_operand->traverse_node(n->data.ternary_operation.left_operand);
  n->data.binary_operation.right_operand->traverse_node(n->data.binary_operation.right_operand);
}

void traverse_unary_operation(struct node * n) { 
  n->data.unary_operation.right_operand->traverse_node(n->data.unary_operation.right_operand);
}

void traverse_expr_list(struct node * n) { 
  n->data.expr_list.init->traverse_node(n->data.expr_list.init);
  n->data.expr_list.expr->traverse_node(n->data.expr_list.expr);
}

void traverse_statement_list(struct node * n) { 
  n->data.statement_list.init->traverse_node(n->data.statement_list.init);
  n->data.statement_list.statement->traverse_node(n->data.statement_list.statement);
}

void traverse_while_statement(struct node * n) { 
  n->data.while_statement.expr->traverse_node(n->data.while_statement.expr);
  n->data.while_statement.statement->traverse_node(n->data.while_statement.statement);
}

void traverse_for_statement(struct node * n) { 
  n->data.for_statement.initial_clause->traverse_node(n->data.for_statement.initial_clause);
  n->data.for_statement.middle_expr->traverse_node(n->data.for_statement.middle_expr);
  n->data.for_statement.end_expr->traverse_node(n->data.for_statement.end_expr);
  n->data.for_statement.statement->traverse_node(n->data.for_statement.statement);
}

void traverse_if_else_statement(struct node * n) { 
  n->data.if_else_statement.expr->traverse_node(n->data.if_else_statement.expr);
  n->data.if_else_statement.statement_if->traverse_node(n->data.if_else_statement.statement_if);
  n->data.if_else_statement.statement_else->traverse_node(n->data.if_else_statement.statement_else);

}

void traverse_if_statement(struct node * n) { 
  n->data.if_statement.expr->traverse_node(n->data.if_statement.expr);
  n->data.if_statement.statement_if->traverse_node(n->data.if_statement.statement_if);
}

void traverse_subscript_decl(struct node * n) { 
  n->data.subscript_decl.decl->traverse_node(n->data.subscript_decl.decl);
  n->data.subscript_decl.const_expr->traverse_node(n->data.subscript_decl.const_expr);
}

void traverse_return_stmt(struct node * n) { 
  n->data.return_stmt.id->traverse_node(n->data.return_stmt.id);
}

void traverse_goto_stmt(struct node * n) { 
  n->data.goto_stmt.id->traverse_node(n->data.goto_stmt.id);
}

void traverse_type_spec(struct node * n) { 
  return;
}

void traverse_function_call(struct node * n) { 
  n->data.function_call.postfix_expr->traverse_node(n->data.function_call.postfix_expr);
  n->data.function_call.expr_list->traverse_node(n->data.function_call.expr_list);
}

void traverse_cast_expr(struct node * n) { 
  n->data.cast_expr.type_expr->traverse_node(n->data.cast_expr.type_expr);
  n->data.cast_expr.expr->traverse_node(n->data.cast_expr.expr);
}

void traverse_comma_expr(struct node * n) { 
  n->data.comma_expr.init->traverse_node(n->data.comma_expr.init);
  n->data.comma_expr.expr->traverse_node(n->data.comma_expr.expr);
}

void traverse_prim_expr(struct node * n) { 
  n->data.prim_expr.expr->traverse_node(n->data.prim_expr.expr);
}

void traverse_func_declarator(struct node * n) { 
  n->data.func_declarator.decl->traverse_node(n->data.func_declarator.decl);
  n->data.func_declarator.param_list->traverse_node(n->data.func_declarator.param_list);
}

void traverse_lbl_stmt(struct node * n) { 
  n->data.lbl_stmt.label->traverse_node(n->data.lbl_stmt.label);
  n->data.lbl_stmt.statement->traverse_node(n->data.lbl_stmt.statement);
}

void traverse_func_def(struct node * n) { 
  n->data.func_def.func_spec->traverse_node(n->data.func_def.func_spec);
  n->data.func_def.stmt->traverse_node(n->data.func_def.stmt);
}

void traverse_func_def_spec(struct node * n) { 
  n->data.func_def_spec.spec->traverse_node(n->data.func_def_spec.spec);
  n->data.func_def_spec.decl->traverse_node(n->data.func_def_spec.decl);
}

void traverse_param_decl(struct node * n) { 
  n->data.param_decl.decl_spec->traverse_node(n->data.param_decl.decl_spec);
  n->data.param_decl.declrtr->traverse_node(n->data.param_decl.declrtr);
}

void traverse_param_list(struct node * n) { 
  n->data.param_list.init->traverse_node(n->data.param_list.init);
  n->data.param_list.param->traverse_node(n->data.param_list.param);
}

void traverse_decl(struct node * n) { 
  /*Here is where the type tree construction begins and the result associated with each item in the decl_list*/
  /*n->data.decl.decl_spec->traverse_node(n->data.decl.decl_spec);*/
  /*n->data.decl.decl_list->traverse_node(n->data.decl.decl_list);*/
  
}

void traverse_decl_list(struct node * n) { 
  n->data.decl_list.init->traverse_node(n->data.decl_list.init);
  n->data.decl_list.decl->traverse_node(n->data.decl_list.decl);
}

void traverse_pointer_decl(struct node * n) { 
  n->data.pointer_decl.decl->traverse_node(n->data.pointer_decl.decl);
}

void traverse_comp_stmt(struct node * n) { 
  /*Compound statement creates a new scope*/
  n->data.comp_stmt.decl_or_stmt_list->traverse_node(n->data.comp_stmt.decl_or_stmt_list);
}

void traverse_expr_stmt(struct node * n) { 
  n->data.expr_stmt.expr->traverse_node(n->data.expr_stmt.expr);
}

void traverse_type_name(struct node * n) { 
  n->data.type_name.decl_spec->traverse_node(n->data.type_name.decl_spec);
  n->data.type_name.abs_decl->traverse_node(n->data.type_name.abs_decl);
}

void traverse_abs_decl(struct node * n) { 
  n->data.abs_decl.dir_abs_decl->traverse_node(n->data.abs_decl.dir_abs_decl);
}

void traverse_translation_unit(struct node * n) { 
  n->data.translation_unit.init->traverse_node(n->data.translation_unit.init);
  n->data.translation_unit.top_level_decl->traverse_node(n->data.translation_unit.top_level_decl);
}

void traverse_null_statement(struct node * n) {
    return;
}

void traverse_break_statement(struct node * n) {
    return;
}

void traverse_continue_statement(struct node * n) {
    return;
}
