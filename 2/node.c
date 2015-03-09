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

/*#define DEBUG*/
/*This is an expanded version of "node" from the in-class example. 
  A node exists for each non-trivial grammar rule, i.e. to store combinations 
  of terminals and nonterminals into a single entity. Some nodes exist for
  trivial rules for cases where the type of node needs to change. For example,
  a primary expression sometimes needs added parens, so it has its own type to 
  make it easier to identify.
  Each node has its own print function, contained in function pointer print_node.
*/

extern int yylineno;

/**
    print_indent - print an indentation for formatting the parse tree

    parameters: output = the stream for output
                depth = the level of indentation. Two spaces for each depth level

    side effects: prints to output

**/

void print_indent(FILE * output, int depth) {
    int i;
    for (i = 0; i < depth; i++) {
        fputs("  ", output);
    }
}

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

/**
    print_number - print a number value

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_number(FILE * output, struct node * n, int depth) {
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

/**
    print_identifier - print an identifier value

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_identifier(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_identifier\n"); 
#endif
    fprintf(output, "%s", n->data.identifier.name);
}

/**
    node_identifier - create an identifier node struct

    parameters: text = string representation of the identifier

    returns: a pointer to the new identifier node

    side effects: memory allocated on heap in call to node_create and to copy the new ident into

**/
struct node *node_identifier(char *text) {
    struct node * nd = node_create(IDENTIFIER, ID);
    nd->data.identifier.name = (char *)malloc(32);
    strncpy(nd->data.identifier.name,text,31);
    nd->print_node=print_identifier;
    nd->line_number = 1;
#ifdef DEBUG
    printf("%s\n",text);
#endif
    return nd;

}

/**
    print_string - print a string value

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_string(FILE * output, struct node * n, int depth) {
    int i;
#ifdef DEBUG
    printf("print_string\n"); 
#endif
    fputs("\"", output);
    for (i=0; i < n->data.string.length; i++) {
        switch (n->data.string.value[i]) {
            /*Prints more cleanly by not using whitespace literally... not un-escaping every character*/
            case '\n':
                fputs("\\n", output);
            break;
            case '\t':
                fputs("\\r", output);
            break;
            case '\v':
                fputs("\\v", output);
            break;
            case '\\':
                fputs("\\\\", output);
            break;
            default:
                fprintf(output,"%c",n->data.string.value[i]);
            break;
        }
    }
    fputs("\"", output);
}

/**
    node_string - create a string node struct

    parameters: text = value of the string
                length = length of the string, stored for cases where null appears in the middle

    returns: a pointer to the new string node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_string(char *text, int length) {
    struct node * nd = node_create(CONSTANT, STRING);
    nd->data.string.value = text;
    nd->data.string.length = length;
    nd->print_node = print_string;
    return nd;
}

/**
    print_ternary_operation - print a ternary operator representation

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_ternary_operation(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_ternary_operation\n"); 
#endif
    fputs("(", output);
    n->data.ternary_operation.left_operand->print_node(output, n->data.ternary_operation.left_operand, depth);
    fputs("?", output);
    n->data.ternary_operation.middle_operand->print_node(output, n->data.ternary_operation.middle_operand, depth);
    fputs(":", output);
    n->data.ternary_operation.right_operand->print_node(output, n->data.ternary_operation.right_operand, depth);
    fputs(")", output);
}

/**
    node_ternary_operation - create a ternary operation node

    parameters: operation1 = token of first operation i.e. ?
                operation2 = token of second operation i.e. :
                left_operand = first expression in operation
                middle_operand = second expression in operation
                right_operand = third expression in operation

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/

struct node *node_ternary_operation(int operation1, int operation2, struct node *left_operand, struct node *middle_operand, struct node *right_operand)
{
  struct node *node = node_create(NODE_TERNARY_OPERATION, operation1);
  node->data.ternary_operation.operation1 = operation1;
  node->data.ternary_operation.operation2 = operation2;
  node->data.ternary_operation.left_operand = left_operand;
  node->data.ternary_operation.middle_operand = middle_operand;
  node->data.ternary_operation.right_operand = right_operand;
  node->data.ternary_operation.result = 0;
  node->print_node=print_ternary_operation;
  return node;
}

/**
    print_binary_op - print a binary operator representation

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_binary_op(FILE * output, struct node * n, int depth) {
    char * op;
#ifdef DEBUG
    printf("print_binary_op\n"); 
#endif
    switch(n->subkind) {
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
            op = "%";
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
            op = "!=";
        break;
        case LEFT:
            op = "<<";
        break;
        case RIGHT:
            op = ">>";
        break;
        case LOGICALAND:
            op = "&&";
        break;
        case LOGICALOR:
            op = "||";
        break;
    }
    
    fputs("(", output);
    n->data.binary_operation.left_operand->print_node(output, n->data.binary_operation.left_operand, depth);
    fputs(op,output);
    n->data.binary_operation.right_operand->print_node(output, n->data.binary_operation.right_operand, depth);
    fputs(")", output);
}


/**
    node_binary_operation - create a binary operation node

    parameters: operation = token of operation
                left_operand = first expression in operation
                right_operand = second expression in operation

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/

struct node *node_binary_operation(int operation, struct node *left_operand,
                                   struct node *right_operand)
{
  struct node *node = node_create(NODE_BINARY_OPERATION, operation);
  node->data.binary_operation.left_operand = left_operand;
  node->data.binary_operation.right_operand = right_operand;
  node->data.binary_operation.result = 0;
  node->print_node=print_binary_op;
  return node;
}

/**
    print_unary_op - print a unary operator value

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_unary_op(FILE * output, struct node * n, int depth) {
    char * op;
#ifdef DEBUG
    printf("print_unary_op\n"); 
#endif
    switch(n->subkind) {
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
    }
    fputs("(", output);
    if (n->data.unary_operation.pre_post == PRE) {
        fputs(op,output);
    }
    n->data.unary_operation.right_operand->print_node(output, n->data.unary_operation.right_operand, depth);
    if (n->data.unary_operation.pre_post == POST) {
        fputs(op,output);
    }
    fputs(")", output);
}

/**
    node_unary_operation - create a unary operation node

    parameters: operation = token of operation
                pre_post = PRE if op comes before id, POST if after
                right_operand = inner expression in operation

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_unary_operation(int operation, int pre_post, struct node *right_operand)
{
  struct node *node = node_create(NODE_UNARY_OPERATION, operation);
  node->data.unary_operation.right_operand = right_operand;
  node->data.unary_operation.pre_post = pre_post;
  node->data.unary_operation.result = 0;
  node->print_node=print_unary_op;
  return node;
}

/**
    print_statement_list - print a statement list. Increase depth of statements in list by 1

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_statement_list(FILE *output, struct node*n, int depth){
  if (NULL != n->data.statement_list.init) {
#ifdef DEBUG
    printf("%d\n", n->data.statement_list.init->kind);
#endif
    n->data.statement_list.init->print_node(output, n->data.statement_list.init, depth);
  }
#ifdef DEBUG
  printf("%d\n", n->data.statement_list.statement->kind);
#endif
  n->data.statement_list.statement->print_node(output, n->data.statement_list.statement, depth+1);
}

/**
    node_statement_list - create a statement list node (linked list)

    parameters: init = pointer to the prior element in the list
                statement = statement for this element in the list

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_statement_list(struct node *init, struct node *statement) {
  struct node *node = node_create(NODE_STATEMENT_LIST, NODE_STATEMENT_LIST);
  node->data.statement_list.init = init;
  node->data.statement_list.statement = statement;
  node->print_node = print_statement_list;
  return node;
}

/**
    print_comma_expr - print a comma expression

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_comma_expr(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_comma_expr\n");
#endif
    if (n->data.comma_expr.init != NULL) {
        n->data.comma_expr.init->print_node(output, n->data.comma_expr.init, depth);
    }
    fputs(",", output);
    n->data.comma_expr.expr->print_node(output, n->data.comma_expr.expr, depth);
}

/**
    node_comma_expr - create a comma_expr node

    parameters: init = pointer to the prior element in the list
                expr = expr for this element in the list

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_comma_expr(struct node *init, struct node *expr) {
  struct node *node = node_create(NODE_COMMA_EXPR, NODE_COMMA_EXPR);
  node->data.comma_expr.init = init;
  node->data.comma_expr.expr = expr;
  node->print_node=print_comma_expr;
  return node;
}

/**
    print_primary_expr - print a primary expression

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_primary_expr(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_primary_expr\n");
#endif
    n->data.prim_expr.expr->print_node(output, n->data.prim_expr.expr, depth);
}

/**
    node_primary_expr - create a primary expression node

    parameters: expr = item in the expression

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_primary_expr(struct node *expr) {
  struct node *node = node_create(NODE_PRIM_EXPR, NODE_PRIM_EXPR);
  node->data.prim_expr.expr = expr;
  node->print_node=print_primary_expr;
  return node;
}


/**
    print_while - print a while statement or do while statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_while(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_while\n");
#endif
    print_indent(output, depth);
    if (n->subkind == WHILE) {
        fputs("while", output);
        if (n->data.while_statement.expr->kind == NODE_PRIM_EXPR) {
            fputs("(", output);
            n->data.while_statement.expr->print_node(output, n->data.while_statement.expr, depth);
            fputs(")", output);
        } else {
            n->data.while_statement.expr->print_node(output, n->data.while_statement.expr, depth);
        }
        fputs("\n", output);
    } else if (n->subkind == DO_WHILE) {
        fputs("do\n", output);
    }
    if (n->data.while_statement.statement->kind != NODE_COMPOUND_STATEMENT) {
        print_indent(output, depth);
        fputs("{\n", output);
        n->data.while_statement.statement->print_node(output, n->data.while_statement.statement, depth+1);
        print_indent(output, depth);
        fputs("}\n", output);
    } else {
        n->data.while_statement.statement->print_node(output, n->data.while_statement.statement, depth);
    }
    if (n->subkind == DO_WHILE) {
        print_indent(output, depth);
        fputs("while ", output);    
        n->data.while_statement.expr->print_node(output, n->data.while_statement.expr, depth);
        fputs(";\n", output);    
    }
}

/**
    node_while_statement - create a while or do-while node

    parameters: while_type = WHILE for while, DO_WHILE for do-while
                expr = expression representing condition for looping
                statement = statement to be executed in loop

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_while_statement(int while_type, struct node *expr, struct node *statement) {
    struct node * node = node_create(NODE_WHILE_STATEMENT, while_type);
    node->data.while_statement.expr = expr;
    node->data.while_statement.statement = statement;
    node->print_node = print_while;
    return node;
}

/**
    print_for - print a for statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_for(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_for\n");
#endif
    print_indent(output, depth);
    fputs("for (", output);
    if (n->data.for_statement.initial_clause != NULL) {
        n->data.for_statement.initial_clause->print_node(output, n->data.for_statement.initial_clause, depth);
    }
    fputs(";", output);
    if (n->data.for_statement.middle_expr != NULL) {
        n->data.for_statement.middle_expr->print_node(output, n->data.for_statement.middle_expr, depth);
    }
    fputs(";", output);
    if (n->data.for_statement.end_expr != NULL) {
        n->data.for_statement.end_expr->print_node(output, n->data.for_statement.end_expr, depth);
    }
    fputs(")\n", output);
    if (n->data.for_statement.statement->kind != NODE_COMPOUND_STATEMENT) {
        print_indent(output, depth);
        fputs("{\n", output);
        n->data.for_statement.statement->print_node(output, n->data.for_statement.statement, depth+1);
        print_indent(output, depth);
        fputs("}\n", output);
    } else {
        n->data.for_statement.statement->print_node(output, n->data.for_statement.statement, depth);
    }
}

/**
    node_for_statement - create a for node

    parameters: initial_clause = first expression in for
                middle_expr = second expression in for
                end_expr = last expression in for
                statement = statement to be looped

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_for_statement(struct node *initial_clause, struct node *middle_expr, struct node* end_expr, struct node *statement) {
    struct node * node = node_create(NODE_FOR_STATEMENT, NODE_FOR_STATEMENT);
    node->data.for_statement.initial_clause = initial_clause;
    node->data.for_statement.middle_expr = middle_expr;
    node->data.for_statement.end_expr = end_expr;
    node->data.for_statement.statement = statement;
    node->print_node = print_for;
    return node;
}

/**
    print_if_else - print an if-else statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_if_else(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_if_else\n");
#endif
    print_indent(output, depth);
    fputs("if", output);
    if (n->data.if_else_statement.expr->kind == NODE_PRIM_EXPR) {
        fputs("(", output);
        n->data.if_else_statement.expr->print_node(output, n->data.if_else_statement.expr, depth);
        fputs(")", output);
    } else {
        n->data.if_else_statement.expr->print_node(output, n->data.if_else_statement.expr, depth);
    }
    fputs("\n", output);

    if (n->data.if_else_statement.statement_if->kind != NODE_COMPOUND_STATEMENT) {

        print_indent(output, depth);
        fputs("{\n", output);
        n->data.if_else_statement.statement_if->print_node(output, n->data.if_else_statement.statement_if, depth+1);
        print_indent(output, depth);
        fputs("}\n", output);

    } else {
        n->data.if_else_statement.statement_if->print_node(output, n->data.if_else_statement.statement_if, depth);
    }

    print_indent(output, depth);

    fputs("else", output);
    fputs("\n", output);

    if (n->data.if_else_statement.statement_else->kind != NODE_COMPOUND_STATEMENT) {
        print_indent(output, depth);
        fputs("{\n", output);
        n->data.if_else_statement.statement_else->print_node(output, n->data.if_else_statement.statement_else, depth+1);
        print_indent(output, depth);
        fputs("}\n", output);
    } else {
        n->data.if_else_statement.statement_else->print_node(output, n->data.if_else_statement.statement_else, depth);
    }


}

/**
    node_if_else_statement - create an if-else statement node

    parameters: expr = condition for if statement
                statement_if = statement in if body
                statement_else = statement in else body

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_if_else_statement(struct node *expr, struct node *statement_if, struct node *statement_else) {
    struct node * node = node_create(NODE_IF_ELSE_STATEMENT, NODE_IF_ELSE_STATEMENT);
    node->data.if_else_statement.expr = expr;
    node->data.if_else_statement.statement_if = statement_if;
    node->data.if_else_statement.statement_else = statement_else;
    node->print_node = print_if_else;
    return node;
}

/**
    print_if - print an if statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_if(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_if\n");
#endif
    print_indent(output, depth);
    fputs("if", output);
    if (n->data.if_statement.expr->kind == NODE_PRIM_EXPR) {
        fputs("(", output);
        n->data.if_statement.expr->print_node(output, n->data.if_else_statement.expr, depth);
        fputs(")", output);
    } else {
        n->data.if_statement.expr->print_node(output, n->data.if_else_statement.expr, depth);
    }
    fputs("\n", output);
    if (n->data.if_statement.statement_if->kind != NODE_COMPOUND_STATEMENT) {
        print_indent(output, depth);
        fputs("{\n", output);
        n->data.if_statement.statement_if->print_node(output, n->data.if_statement.statement_if, depth+1);
        print_indent(output, depth);
        fputs("}\n", output);
    } else {
        n->data.if_statement.statement_if->print_node(output, n->data.if_statement.statement_if, depth);
    }
}

/**
    node_if_statement - create an if statement

    parameters: expr = condition for if statement
                statement_if = statement in if body

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_if_statement(struct node *expr, struct node *statement_if) {
    struct node * node = node_create(NODE_IF_STATEMENT, NODE_IF_STATEMENT);
    node->data.if_statement.expr = expr;
    node->data.if_statement.statement_if = statement_if;
    node->print_node = print_if;
    return node;
}

/**
    print_subscript_decl - print a subscript declarator e.g. a[b]

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_subscript_decl(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_subscript_decl\n");
#endif
    fputs("(", output);
    if (n->data.subscript_decl.decl != NULL) {
        n->data.subscript_decl.decl->print_node(output, n->data.subscript_decl.decl, depth);
    }
    fputs("[", output);
    if (n->data.subscript_decl.const_expr != NULL) {
        n->data.subscript_decl.const_expr->print_node(output, n->data.subscript_decl.const_expr, depth);
    }
    fputs("]", output);
    fputs(")", output);
}

/**
    node_subscript_decl - create a subscript declarator

    parameters: decl = declarator to the left of brackets
                const_expr = constant within brackets

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_subscript_decl(struct node * decl, struct node *const_expr) {
    struct node * node = node_create(NODE_SUBSCRIPT_STATEMENT, NODE_SUBSCRIPT_STATEMENT);
    node->data.subscript_decl.decl = decl;
    node->data.subscript_decl.const_expr = const_expr;
    node->print_node=print_subscript_decl;
    return node;
}

/**
    print_break - print a break statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_node_break(FILE * output, struct node * n, int depth) {
    print_indent(output, depth);
    fputs("break;\n", output);
}

/**
    node_break - create a break node

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_break() {
    struct node * node = node_create(NODE_BREAK, NODE_BREAK);
    node->print_node=print_node_break;
    return node;
}

/**
    print_node_continue - print a continue statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_node_continue(FILE * output, struct node * n, int depth) {
    print_indent(output, depth);
    fputs("continue;\n", output);
}

/**
    node_continue - create a continue node

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_continue() {
    struct node * node = node_create(NODE_CONTINUE, NODE_CONTINUE);
    node->print_node=print_node_continue;
    return node;
}

/**
    print_node_return - print a return statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_node_return(FILE * output, struct node * n, int depth) {
    print_indent(output, depth);
    fputs("return", output);
    if (n->data.return_stmt.id != NULL) {
        fputs(" ", output);
        n->data.return_stmt.id->print_node(output, n->data.return_stmt.id, depth);
    }
    fputs(";\n", output);
}

/**
    node_return - create a return node

    parameters: id = return value

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_return(struct node* id) {
    struct node * node = node_create(NODE_RETURN, NODE_RETURN);
    node->data.return_stmt.id = id;
    node->print_node=print_node_return;
    return node;
}

/**
    print_node_goto - print a goto statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_node_goto(FILE * output, struct node * n, int depth) {
    print_indent(output, depth);
    fputs("goto ", output);
    n->data.return_stmt.id->print_node(output, n->data.return_stmt.id, depth);
    fputs(";\n", output);
}

/**
    node_goto - create a goto node

    parameters: id = label to go to

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_goto(struct node* id) {
    struct node * node = node_create(NODE_GOTO, NODE_GOTO);
    node->data.goto_stmt.id = id;
    node->print_node=print_node_goto;
    return node;
}

/**
    print_type_spec - print a canonical type specifier

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_type_spec(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_type\n"); 
#endif
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

    fprintf(output, "%s", type_str[n->subkind]);

}

/**
    node_type_spec - create a type specifier

    parameters: type = base_types enum value

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_type_spec(int type) {
    struct node * node = node_create(NODE_TYPE, type);
    node->data.type_spec.type = type;
    node->print_node = print_type_spec;
    return node;
}

/**
    print_function_call - print a function_call

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_function_call(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_function_call\n");
#endif
    fputs("(", output);
    n->data.function_call.postfix_expr->print_node(output, n->data.function_call.postfix_expr, depth);
    if (n->data.function_call.expr_list != NULL) {
        /*print ( ) here if the paramaters were in a list, or if it's a primary expression, else they just come from the expression*/
        if (n->data.function_call.expr_list->data.expr_list.init != NULL || n->data.function_call.expr_list->data.expr_list.expr->kind == NODE_PRIM_EXPR) {
            fputs("(", output);
            n->data.function_call.expr_list->print_node(output, n->data.function_call.expr_list, depth);
            fputs(")", output);
        } else {
            n->data.function_call.expr_list->print_node(output, n->data.function_call.expr_list, depth);
        }
    }
    fputs(")", output);
}

/**
    node_function_call - create a function call node

    parameters: postfix_expr = id of function call
                expr_list = arguments to function call

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_function_call(struct node* postfix_expr, struct node* expr_list) {
    struct node * node = node_create(NODE_FUNCTION_CALL, NODE_FUNCTION_CALL);
    node->data.function_call.postfix_expr = postfix_expr;
    node->data.function_call.expr_list = expr_list;
    node->print_node=print_function_call;
    return node;

}

/**
    print_cast_expr - print a cast expression

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_cast_expr(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_cast_expr\n");
#endif
    fputs("(", output);
    n->data.cast_expr.type_expr->print_node(output, n->data.cast_expr.type_expr, depth);
    fputs(")", output);
    n->data.cast_expr.expr->print_node(output, n->data.cast_expr.expr, depth);
}

/**
    node_cast_expr - create a cast expression node

    parameters: type_expr = expression representing new type
                expr = expression to be cast

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_cast_expr(struct node* type_expr, struct node* expr) {
    struct node * node = node_create(NODE_CAST_EXPR, NODE_CAST_EXPR);
    node->data.cast_expr.type_expr = type_expr;
    node->data.cast_expr.expr = expr;
    node->print_node=print_cast_expr;
    return node;

}

/**
    print_func_decl - print a function declarator

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_func_decl(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_func_decl\n"); 
#endif
    n->data.func_declarator.decl->print_node(output, n->data.func_declarator.decl, depth);
    fputs("(",output);
    n->data.func_declarator.param_list->print_node(output, n->data.func_declarator.param_list, depth);
    fputs(")",output);
}


/**
    node_func_declarator - create a function declarator node

    parameters: decl = declarator id
                param_list = list of parameters for the function

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_func_declarator(struct node *decl, struct node *param_list) {
    struct node * node = node_create(NODE_FUNCTION_DECL, NODE_FUNCTION_DECL);
    node->data.func_declarator.decl = decl;
    node->data.func_declarator.param_list = param_list;
    node->print_node=print_func_decl;
    return node;
}

/**
    print_func_def - print a function definition

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_func_def(FILE * output, struct node * n, int depth) {
    n->data.func_def.func_spec->print_node(output, n->data.func_def.func_spec, depth);
    fputs("\n",output);
    n->data.func_def.stmt->print_node(output, n->data.func_def.stmt, depth);
}

/**
    node_function_def - create a function definition node

    parameters: func_spec = function spec
                stmt = function body

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_function_def(struct node* func_spec, struct node* stmt) {
    struct node * node = node_create(NODE_FUNCTION_DEF, NODE_FUNCTION_DEF);
    node->data.func_def.func_spec = func_spec;
    node->data.func_def.stmt = stmt;
    node->print_node=print_func_def;
    return node;
}

/**
    print_func_def_spec - print a function definition specifier

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_func_def_spec(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_func_def_spec\n"); 
#endif
    n->data.func_def_spec.spec->print_node(output, n->data.func_def_spec.spec, depth);
    fputs(" ", output);
    n->data.func_def_spec.decl->print_node(output, n->data.func_def_spec.decl, depth);
}

/**
    node_function_def_spec - create a function definition specifier node

    parameters: spec = function specifier
                decl = decl of function

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_function_def_spec(struct node* spec, struct node* decl) {
    struct node * node = node_create(NODE_FUNCTION_DEF_SPEC, NODE_FUNCTION_DEF_SPEC);
    node->data.func_def_spec.spec = spec;
    node->data.func_def_spec.decl = decl;
    node->print_node=print_func_def_spec;
    return node;
}

/**
    print_labeled_statement - print a labeled statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_labeled_statement(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_labeled_statemnet\n"); 
#endif
    print_indent(output, depth);
    n->data.lbl_stmt.label->print_node(output, n->data.lbl_stmt.label, depth);
    fputs(":\n", output);
    n->data.lbl_stmt.statement->print_node(output, n->data.lbl_stmt.statement, depth+1);
    
}

/**
    node_labeled_statement - create a labeled statement node

    parameters: label = label id
                statement = statement being labeled

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_labeled_statement(struct node *label, struct node *statement) {
    struct node * node = node_create(NODE_LABELED_STMT, NODE_LABELED_STMT);
    node->data.lbl_stmt.label = label;
    node->data.lbl_stmt.statement = statement;
    node->print_node=print_labeled_statement;
    return node;
}

/**
    print_param_list - print a parameter list

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_param_list(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_param_list\n"); 
#endif
    if (NULL != n->data.param_list.init) {
        n->data.param_list.init->print_node(output, n->data.param_list.init, depth);
        fputs(",", output);
    }
    n->data.param_list.param->print_node(output, n->data.param_list.param, depth);
}

/**
    node_param_list - create a parameter list node

    parameters: init = pointer to previous item in linked list
                param = parameter at this item in the list

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_param_list(struct node *init, struct node *param){
    struct node * node = node_create(NODE_PARAM_LIST, NODE_PARAM_LIST);
    node->data.param_list.init = init;
    node->data.param_list.param = param;
    node->print_node=print_param_list;
    return node;
}

/**
    print_param_decl - print a parameter decl

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_param_decl(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_param_list\n"); 
#endif
    n->data.param_decl.decl_spec->print_node(output, n->data.param_decl.decl_spec, depth);
    if (n->data.param_decl.declrtr != NULL) {
        fputs(" ", output);
        n->data.param_decl.declrtr->print_node(output, n->data.param_decl.declrtr, depth);
    }
}

/**
    node_param_decl - create a parameter decl node

    parameters: decl_spec = declaration specifier of parameter
                declrtr = declarator of parameter

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_param_decl(struct node *decl_spec, struct node *declrtr){
    struct node * node = node_create(NODE_PARAM_DECL, NODE_PARAM_DECL);
    node->data.param_decl.decl_spec = decl_spec;
    node->data.param_decl.declrtr = declrtr;
    node->print_node=print_param_decl;
    return node;
}

/**
    print_decl - print a decl

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_decl(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_node_decl\n");
#endif
    print_indent(output, depth);
    n->data.decl.decl_spec->print_node(output, n->data.decl.decl_spec, depth);
    fputs(" ", output);
    n->data.decl.decl_list->print_node(output, n->data.decl.decl_list, depth);
    fputs(";\n", output);
}

/**
    node_decl - create a decl node

    parameters: decl_spec = decl specifier
                decl_list = list of declarators

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_decl(struct node * decl_spec, struct node * decl_list) {
    struct node * node = node_create(NODE_DECL, NODE_DECL);
    node->data.decl.decl_spec = decl_spec;
    node->data.decl.decl_list = decl_list;
    node->print_node=print_decl;
    return node;
}

/**
    print_decl_list - print a decl list

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_decl_list(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_decl_list\n"); 
#endif
    if (NULL != n->data.decl_list.init) {
        n->data.decl_list.init->print_node(output, n->data.decl_list.init, depth);
        fputs(", ", output);
    }
    n->data.decl_list.decl->print_node(output, n->data.decl_list.decl, depth);
}

/**
    node_decl_list - create a decl list node

    parameters: init = pointer to previous item in linked list
                decl = decl at this item in list

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_decl_list(struct node * init, struct node * decl) {
    struct node * node = node_create(NODE_DECL_LIST, NODE_DECL_LIST);
    node->data.decl_list.init = init;
    node->data.decl_list.decl = decl;
    node->print_node=print_decl_list;
    return node;
}

/**
    print_pointer_decl - print a pointer declarator

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_pointer_decl(FILE * output, struct node * n, int depth) {
    int i;
#ifdef DEBUG
    printf("print_node_decl_list\n"); 
#endif

    for (i = 0; i < n->data.pointer_decl.depth; i++) {
        fputs("(*",output);
    }
    n->data.pointer_decl.decl->print_node(output, n->data.pointer_decl.decl, depth);
    for (i = 0; i < n->data.pointer_decl.depth; i++) {
        fputs(")",output);
    }
}

/**
    node_pointer_decl - create a pointer declarator

    parameters: spec = depth of the pointer e.g. *** is depth 3
                decl = decl of pointer

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_pointer_decl(int depth, struct node * decl) {
    struct node * node = node_create(NODE_POINTER_DECL, NODE_POINTER_DECL);
    node->data.pointer_decl.depth = depth;
    node->data.pointer_decl.decl = decl;
    node->print_node=print_pointer_decl;
    return node;
}

/**
    print_compound_statement - print a compound statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_compound_statement(FILE * output, struct node * n, int depth) {
    print_indent(output, depth);
    fputs("{\n",output);
    if (n->data.comp_stmt.decl_or_stmt_list != NULL) {
        n->data.comp_stmt.decl_or_stmt_list->print_node(output,n->data.comp_stmt.decl_or_stmt_list, depth);
    }
    print_indent(output, depth);
    fputs("}\n",output);
}

/**
    node_compound_statement - create a compound statement node

    parameters: decl_or_stmt_list = declarations or statements in body

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_compound_statement(struct node *decl_or_stmt_list) {
    struct node * node = node_create(NODE_COMPOUND_STATEMENT, NODE_COMPOUND_STATEMENT);
    node->data.comp_stmt.decl_or_stmt_list = decl_or_stmt_list;
    node->print_node=print_compound_statement;
    return node; 
}

/**
    print_expr_statement - print an expression statement

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_expr_statement(FILE * output, struct node * n, int depth) {
    print_indent(output, depth);
    n->data.expr_stmt.expr->print_node(output, n->data.expr_stmt.expr, depth);
    fputs(";\n",output);
}

/**
    node_expr_statement - create an expression statement node

    parameters: expr = expression in statement

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_expr_statement(struct node *expr) {
    struct node * node = node_create(NODE_EXPR_STATEMENT, NODE_EXPR_STATEMENT);
    node->data.expr_stmt.expr = expr;
    node->print_node=print_expr_statement;
    return node; 
}

/**
    print_null_statement - print a null statement (;)

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_null_statement(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_null_statement\n"); 
#endif
    print_indent(output, depth);
    fputs(";\n",output);
}

/**
    node_null_statement - create a null statement node

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_null_statement() {
    struct node * node = node_create(NODE_NULL_STATEMENT, NODE_NULL_STATEMENT);
    node->print_node=print_null_statement;
    return node;
}

/**
    print_type_name - print a type name

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_type_name(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_type_name\n"); 
#endif
    n->data.type_name.decl_spec->print_node(output,n->data.type_name.decl_spec,depth);
    if (NULL != n->data.type_name.abs_decl) {
        n->data.type_name.abs_decl->print_node(output,n->data.type_name.abs_decl,depth);
    }
}

/**
    node_type_name - create a type name node

    parameters: decl_spec = declaration specifier
                abs_decl = abstract declarator

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_type_name(struct node * decl_spec, struct node * abs_decl){
    struct node * node = node_create(NODE_TYPE_NAME, NODE_TYPE_NAME);
    node->data.type_name.decl_spec = decl_spec;
    node->data.type_name.abs_decl = abs_decl;
    node->print_node=print_type_name;
    return node;
}

/**
    print_abstract_decl - print an abstract decl

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_abstract_decl(FILE * output, struct node * n, int depth) {
    int i;
#ifdef DEBUG
    printf("print_abstract_decl\n"); 
#endif

    for (i = 0; i < n->data.abs_decl.pointer_depth; i++) {
        fputs("(*",output);
    }
    if (n->data.abs_decl.dir_abs_decl != NULL) {
        n->data.abs_decl.dir_abs_decl->print_node(output, n->data.abs_decl.dir_abs_decl, depth);
    }
    for (i = 0; i < n->data.abs_decl.pointer_depth; i++) {
        fputs(")",output);
    }
    
}

/**
    node_abstract_decl - create an abstract declarator node

    parameters: pointer_depth = depth of pointer e.g. *** = 3
                dir_abs_decl = direct abstract declarator

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_abstract_decl(long int pointer_depth, struct node * dir_abs_decl) {
    struct node * node = node_create(NODE_TYPE_NAME, NODE_TYPE_NAME);
    node->data.abs_decl.pointer_depth = pointer_depth;
    node->data.abs_decl.dir_abs_decl = dir_abs_decl;
    node->print_node=print_abstract_decl;
    return node;
}

/**
    print_translation_unit - print a translation unit

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_translation_unit(FILE * output, struct node * n, int depth) {
#ifdef DEBUG
    printf("print_translation_unit\n"); 
#endif
    if (NULL != n->data.translation_unit.init) {
        n->data.translation_unit.init->print_node(output, n->data.translation_unit.init, depth);
    }
    n->data.translation_unit.top_level_decl->print_node(output, n->data.translation_unit.top_level_decl, depth);
}

/**
    node_translation_unit - create a translation unit node

    parameters: init = pointer to previous item in linked list
                top_level_decl = declaration or function definition for this item

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_translation_unit(struct node * init, struct node * top_level_decl){
    struct node * node = node_create(NODE_TRANSLATION_UNIT, NODE_TRANSLATION_UNIT);
    node->data.translation_unit.init = init;
    node->data.translation_unit.top_level_decl = top_level_decl;
    node->print_node=print_translation_unit;
    return node;

}

/**
    print_expr_list - print an expression list

    parameters: output = the stream for output
                n = pointer to node struct to be printed
                depth = the level of indentation. Used only for statements/declarations

    side effects: prints to output

**/

void print_expr_list(FILE *output, struct node*n, int depth){
  if (NULL != n->data.expr_list.init) {
#ifdef DEBUG
    printf("%d\n", n->data.expr_list.init->kind);
#endif
    n->data.expr_list.init->print_node(output, n->data.expr_list.init, depth);
    fputs(",", output);
  } else {
  }
#ifdef DEBUG
  printf("%d\n", n->data.expr_list.expr->kind);
#endif
  n->data.expr_list.expr->print_node(output, n->data.expr_list.expr, depth+1);
}

/**
    node_expr_list - create an expression list node

    parameters: init = pointer to previous item in linked list
                expr = expression in list

    returns: a pointer to the new node

    side effects: memory allocated on heap in call to node_create

**/
struct node *node_expr_list(struct node *init, struct node *expr) {
  struct node *node = node_create(NODE_EXPR_LIST, NODE_EXPR_LIST);
  node->data.expr_list.init = init;
  node->data.expr_list.expr = expr;
  node->print_node = print_expr_list;
  return node;
}

