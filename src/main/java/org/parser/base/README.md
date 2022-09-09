# Parser explanation

### Or-Parser
The or-parser returns the AST of the first successful subparser.

### Concat-Parser
The concat-parser concatenates the given subparsers. The concat-parser is successful
if all the subparsers are successful. The building of the resulting AST (atSuccess-method) 
can be done in many ways. In the ``org.parser.base.build.Mode`` class are some inbuilt atSuccess-methods.

#### Modes
##### childrenIfNoType
When one uses the childrenIfNoType atSuccess-method, the resulting AST has the given type.
The list of children is generated in the following way: If the AST (AST of subparser) has a type of null then the
children of the AST are added to the list of children, otherwise the AST itself is taken over as a child.

This is especially useful if one is using many-/some-parsers, since then the children of the
many-/some-parser can be taken over directly without specifying another type.

The childrenIfNoType atSuccess-method is the standard if you specify the type with ``.type(TYPE)`` in the Rule-class.

##### justFst
When one uses the justFst atSuccess-method, the resulting AST is just the AST of the first subparser.

This is especially useful if one wraps many hide-subparser around one subparser with a type (since the 
hide-parser are ignored and therefore do not appear in the list of ASTs).

The justFst atSuccess-method is the standard if no type is specified.

##### all
When one uses the all atSuccess-method, the resulting AST has the given type and the list of children
is just the list of ASTs from all subparsers (no matter what the type is).


### Many-Parser

### Placeholder-Parser

### RegEx-Parser
#### match-Parser
#### hide-Parser
#### keyword-Parser

# Build-System