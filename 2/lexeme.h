#ifndef _NODE_H
#define _NODE_H

#include <stdio.h>
#include <stdbool.h>

/*This is a butchered version of "node" from the in-class example. 
  It stores the node values and other attributes to be set to 
  the yylval variable after calling yylex.
  These objects will likely be used later as parts of nodes.
*/

struct node {
  int kind; /*kinds enum*/
  int subkind; /*resvwords, ops, or tokens enum*/
  int line_number;
  union {
    struct {
      unsigned long value;
    } number;
    struct {
      char * name;
    } identifier;
    struct {
      char * value;
      int length;
    } string;
  } data;
};

/* Constructors */
struct node *node_number(long int value, int subkind);
struct node *node_identifier(char *text);
struct node *node_string(char *text, int length);

#endif
