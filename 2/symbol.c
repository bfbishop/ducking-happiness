#include <stdlib.h>
#include "node.h"
#include "symbol.h"
/*#define DEBUG*/

struct scope_tree_node unsigned_char = {TYPE, BASE, {{TYPE_UCHAR}}};
struct scope_tree_node signed_char = {TYPE, BASE, {{TYPE_SCHAR}}};
struct scope_tree_node unsigned_short = {TYPE, BASE, {{TYPE_USHORT}}};
struct scope_tree_node signed_short = {TYPE, BASE, {{TYPE_SSHORT}}};
struct scope_tree_node unsigned_int = {TYPE, BASE, {{TYPE_UINT}}};
struct scope_tree_node signed_int = {TYPE, BASE, {{TYPE_SINT}}};
struct scope_tree_node unsigned_long = {TYPE, BASE, {{TYPE_ULONG}}};
struct scope_tree_node signed_long = {TYPE, BASE, {{TYPE_SLONG}}};
struct scope_tree_node _void = {TYPE, BASE, {{TYPE_VOID}}};


struct scope_tree_node * type_node_create(int scope_tree_kind, int type_kind) {
    struct scope_tree_node * t;
    t = malloc(sizeof(struct scope_tree_node));
    t->kind = scope_tree_kind;
    t->type_kind = type_kind;
    return t;
}

int lookup_id(char * id) {
    return 9;
}

int lookup_label(char * id) {
    return 9;
}

struct scope_tree_node * traverse_number(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_number\n");
  #endif

  return parent_scope;
}

struct scope_tree_node * traverse_identifier(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_identifier\n");
  #endif

  lookup_id(n->data.identifier.name);
  return parent_scope;
}

struct scope_tree_node * traverse_string(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_string\n");
  #endif

  return parent_scope;
}

struct scope_tree_node * traverse_ternary_operation(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_ternary_operation\n");
  #endif

  n->data.ternary_operation.left_operand->traverse_node(n->data.ternary_operation.left_operand, parent_scope);


  n->data.ternary_operation.middle_operand->traverse_node(n->data.ternary_operation.middle_operand, parent_scope);


  n->data.ternary_operation.right_operand->traverse_node(n->data.ternary_operation.right_operand, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_binary_operation(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_binary_operation\n");
  #endif

  n->data.binary_operation.left_operand->traverse_node(n->data.ternary_operation.left_operand, parent_scope);


  n->data.binary_operation.right_operand->traverse_node(n->data.binary_operation.right_operand, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_unary_operation(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_unary_operation\n");
  #endif

  n->data.unary_operation.right_operand->traverse_node(n->data.unary_operation.right_operand, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_expr_list(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_expr_list\n");
  #endif

  if (n->data.expr_list.init != NULL) {
    n->data.expr_list.init->traverse_node(n->data.expr_list.init, parent_scope);
  }

  n->data.expr_list.expr->traverse_node(n->data.expr_list.expr, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_statement_list(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_statement_list\n");
  #endif

  if (n->data.statement_list.init != NULL) {
    n->data.statement_list.init->traverse_node(n->data.statement_list.init, parent_scope);
  }

  n->data.statement_list.statement->traverse_node(n->data.statement_list.statement, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_while_statement(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_while_statement\n");
  #endif

  n->data.while_statement.expr->traverse_node(n->data.while_statement.expr, parent_scope);


  n->data.while_statement.statement->traverse_node(n->data.while_statement.statement, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_for_statement(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_for_statement\n");
  #endif

  if (n->data.for_statement.initial_clause != NULL) {
    n->data.for_statement.initial_clause->traverse_node(n->data.for_statement.initial_clause, parent_scope);
  }

  if (n->data.for_statement.middle_expr != NULL) {
    n->data.for_statement.middle_expr->traverse_node(n->data.for_statement.middle_expr, parent_scope);
  }

  if (n->data.for_statement.end_expr != NULL) {
    n->data.for_statement.end_expr->traverse_node(n->data.for_statement.end_expr, parent_scope);

  }
  n->data.for_statement.statement->traverse_node(n->data.for_statement.statement, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_if_else_statement(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_if_else_statement\n");
  #endif

  n->data.if_else_statement.expr->traverse_node(n->data.if_else_statement.expr, parent_scope);


  n->data.if_else_statement.statement_if->traverse_node(n->data.if_else_statement.statement_if, parent_scope);


  n->data.if_else_statement.statement_else->traverse_node(n->data.if_else_statement.statement_else, parent_scope);



  return parent_scope;
}

struct scope_tree_node * traverse_if_statement(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_if_statement\n");
  #endif

  n->data.if_statement.expr->traverse_node(n->data.if_statement.expr, parent_scope);


  n->data.if_statement.statement_if->traverse_node(n->data.if_statement.statement_if, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_subscript_decl(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_subscript_decl\n");
  #endif
  struct scope_tree_node * array_type = type_node_create(TYPE, POINTER);

  if (n->data.subscript_decl.decl != NULL) {
    array_type->data_types.array.declarator = n->data.subscript_decl.decl->traverse_node(n->data.subscript_decl.decl, array_type);
  }

  if (n->data.subscript_decl.const_expr != NULL) {
    array_type->data_types.array.const_expr = n->data.subscript_decl.const_expr->traverse_node(n->data.subscript_decl.const_expr, array_type);
  }

  return array_type;
/*
  if (n->data.subscript_decl.decl != NULL) {
    n->data.subscript_decl.decl->traverse_node(n->data.subscript_decl.decl, parent_scope);
  }

  if (n->data.subscript_decl.const_expr != NULL) {
    n->data.subscript_decl.const_expr->traverse_node(n->data.subscript_decl.const_expr, parent_scope);
  }
  return parent_scope;
*/
}

struct scope_tree_node * traverse_return_stmt(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_return_stmt\n");
  #endif

  if (n->data.return_stmt.id != NULL) {
    n->data.return_stmt.id->traverse_node(n->data.return_stmt.id, parent_scope);
  }

  return parent_scope;
}

struct scope_tree_node * traverse_goto_stmt(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_goto_stmt\n");
  #endif

  n->data.goto_stmt.id->traverse_node(n->data.goto_stmt.id, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_type_spec(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_type_spec\n");
  #endif
 
  struct scope_tree_node * base_type_node = NULL;
   
  switch(n->data.type_spec.type) {
        case TYPE_VOID:
            base_type_node = &_void;
        break;
        case TYPE_SCHAR:
            base_type_node = &signed_char;
        break;
        case TYPE_UCHAR:
            base_type_node = &unsigned_char;
        break;
        case TYPE_SSHORT:
            base_type_node = &signed_short;
        break;
        case TYPE_USHORT:
            base_type_node = &unsigned_short;
        break;
        case TYPE_SINT:
            base_type_node = &signed_int;
        break;
        case TYPE_UINT:
            base_type_node = &unsigned_int;
        break;
        case TYPE_SLONG:
            base_type_node = &signed_long;
        break;
        case TYPE_ULONG:
            base_type_node = &unsigned_long;
        break;
  }

  return base_type_node;
}

struct scope_tree_node * traverse_function_call(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_function_call\n");
  #endif

  n->data.function_call.postfix_expr->traverse_node(n->data.function_call.postfix_expr, parent_scope);

  if (n->data.function_call.expr_list != NULL) {
    n->data.function_call.expr_list->traverse_node(n->data.function_call.expr_list, parent_scope);
  }

  return parent_scope;
}

struct scope_tree_node * traverse_cast_expr(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_cast_expr\n");
  #endif

  n->data.cast_expr.type_expr->traverse_node(n->data.cast_expr.type_expr, parent_scope);


  n->data.cast_expr.expr->traverse_node(n->data.cast_expr.expr, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_comma_expr(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_comma_expr\n");
  #endif

  n->data.comma_expr.init->traverse_node(n->data.comma_expr.init, parent_scope);


  n->data.comma_expr.expr->traverse_node(n->data.comma_expr.expr, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_prim_expr(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_prim_expr\n");
  #endif

  n->data.prim_expr.expr->traverse_node(n->data.prim_expr.expr, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_func_declarator(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_func_declarator\n");
  #endif

  n->data.func_declarator.decl->traverse_node(n->data.func_declarator.decl, parent_scope);


  n->data.func_declarator.param_list->traverse_node(n->data.func_declarator.param_list, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_lbl_stmt(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_lbl_stmt\n");
  #endif

  n->data.lbl_stmt.label->traverse_node(n->data.lbl_stmt.label, parent_scope);


  n->data.lbl_stmt.statement->traverse_node(n->data.lbl_stmt.statement, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_func_def(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_func_def\n");
  #endif

  n->data.func_def.func_spec->traverse_node(n->data.func_def.func_spec, parent_scope);


  n->data.func_def.stmt->traverse_node(n->data.func_def.stmt, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_func_def_spec(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_func_def_spec\n");
  #endif

  n->data.func_def_spec.spec->traverse_node(n->data.func_def_spec.spec, parent_scope);


  n->data.func_def_spec.decl->traverse_node(n->data.func_def_spec.decl, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_param_decl(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_param_decl\n");
  #endif

  n->data.param_decl.decl_spec->traverse_node(n->data.param_decl.decl_spec, parent_scope);

  if (n->data.param_decl.declrtr != NULL) {
    n->data.param_decl.declrtr->traverse_node(n->data.param_decl.declrtr, parent_scope);
  }

  return parent_scope;
}

struct scope_tree_node * traverse_param_list(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_param_list\n");
  #endif
  if (n->data.param_list.init != NULL) {
    n->data.param_list.init->traverse_node(n->data.param_list.init, parent_scope);
  }

  n->data.param_list.param->traverse_node(n->data.param_list.param, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_decl(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_decl\n");
  #endif

  /*Here is where the type tree construction begins and the result associated with each item in the decl_list*/
  /*n->data.decl.decl_spec->traverse_node(n->data.decl.decl_spec, parent_scope);*/


  /*n->data.decl.decl_list->traverse_node(n->data.decl.decl_list, parent_scope);*/


  
  return parent_scope;
}

struct scope_tree_node * traverse_decl_list(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_decl_list\n");
  #endif

  if (n->data.decl_list.init != NULL) {
    n->data.decl_list.init->traverse_node(n->data.decl_list.init, parent_scope);
  }

  n->data.decl_list.decl->traverse_node(n->data.decl_list.decl, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_pointer_decl(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_pointer_decl\n");
  #endif

  struct scope_tree_node * pointer_type = type_node_create(TYPE, POINTER);

  pointer_type->data_types.pointer.pointer_depth = n->data.pointer_decl.depth;
  pointer_type->data_types.pointer.declarator = n->data.pointer_decl.decl->traverse_node(n->data.pointer_decl.decl, pointer_type);


  return pointer_type;
}

struct scope_tree_node * traverse_comp_stmt(struct node * n, struct scope_tree_node * parent_scope) { 
  /*Compound statement creates a new scope*/
  struct scope_tree_node * new_scope = type_node_create(NODE_COMPOUND_STATEMENT, SCOPE);
  #ifdef DEBUG
  printf("traverse_comp_stmt\n");
  #endif

  new_scope->data_types.scope.parent_scope = parent_scope;
  printf("{\n");
  if (n->data.comp_stmt.decl_or_stmt_list != NULL) {
    n->data.comp_stmt.decl_or_stmt_list->traverse_node(n->data.comp_stmt.decl_or_stmt_list, new_scope);
  }

  printf("}\n");
  return new_scope;
}

struct scope_tree_node * traverse_expr_stmt(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_expr_stmt\n");
  #endif

  n->data.expr_stmt.expr->traverse_node(n->data.expr_stmt.expr, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_type_name(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_type_name\n");
  #endif

  n->data.type_name.decl_spec->traverse_node(n->data.type_name.decl_spec, parent_scope);

  if (n->data.type_name.abs_decl != NULL) {
    n->data.type_name.abs_decl->traverse_node(n->data.type_name.abs_decl, parent_scope);
  }

  return parent_scope;
}

struct scope_tree_node * traverse_abs_decl(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_abs_decl\n");
  #endif

  /*int pointer_depth = 0;*/

  if (n->data.abs_decl.dir_abs_decl != NULL) {
    n->data.abs_decl.dir_abs_decl->traverse_node(n->data.abs_decl.dir_abs_decl, parent_scope);
  }

  return parent_scope;
}

struct scope_tree_node * traverse_translation_unit(struct node * n, struct scope_tree_node * parent_scope) { 
  #ifdef DEBUG
  printf("traverse_translation_unit\n");
  #endif
  if (n->data.translation_unit.init != NULL) {
    n->data.translation_unit.init->traverse_node(n->data.translation_unit.init, parent_scope);
  }

  n->data.translation_unit.top_level_decl->traverse_node(n->data.translation_unit.top_level_decl, parent_scope);


  return parent_scope;
}

struct scope_tree_node * traverse_null_statement(struct node * n, struct scope_tree_node * parent_scope) {
  #ifdef DEBUG
  printf("traverse_null_statement\n");
  #endif

  return parent_scope;
}

struct scope_tree_node * traverse_break_statement(struct node * n, struct scope_tree_node * parent_scope) {
  #ifdef DEBUG
  printf("traverse_break_statement\n");
  #endif

  return parent_scope;
}

struct scope_tree_node * traverse_continue_statement(struct node * n, struct scope_tree_node * parent_scope) {
  #ifdef DEBUG
  printf("traverse_continue_statement\n");
  #endif

  return parent_scope;
}
