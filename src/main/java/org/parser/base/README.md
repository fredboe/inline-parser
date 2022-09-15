# Parser explanation

### Or-Parser
The or-parser returns the AST of the first successful subparser.

### Concat-Parser
The concat-parser concatenates the given subparsers. The concat-parser is successful
if all the subparsers are successful. The building of the resulting AST (atSuccess-method) 
can be done in many ways. In the `org.parser.base.build.Mode` class are some inbuilt atSuccess-methods.

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
The many-parser parses the input until its subparser fails. Then a new AST
is created with the given type and the successful passes as children. An 
important note is that when a child has a type of 'null' it's ignored.

### Placeholder-Parser
A placeholder-parser just execute it's subparser without adding any functionality.
The placeholder-parser is mainly used in the building process.

### RegEx-Parser
A regex-parser tries to match the given regex (with lookingAt) and then consumes
the matched string.

#### match-parser
The match-parser is a regex-parser that returns an AST with the given type.

#### hide-parser
The hide-parser is a regex-parser that sets the ignore-bit in the resulting AST
to 1.

#### keyword-parser
The keyword-parser is a regex-parser that returns an AST with the given type, but
it doesn't store the matched string.

## Build system
With the parser builder one can easily create a parser for a grammar.
In the parser builder one creates many (named) rules. A rule consists of one or-parser
with many subparsers (concat, many, etc.). Sometimes it's also beneficial to create
subsubrules (for example, if one wants to create a many-rule without creating the subrule with a name). 
These subsubrules, can be created with the Simplerule-class.

Maybe one last note on the `type`-method: The `type`-method allows one to specify the behavior of the subrule.
The default behavior is "justFst" (for an explanation, see above). If one calls the `type`-method with a type-object 
then the behavior changes to "childrenIfNoType" (for an explanation, see above). It's also possible to specify a custom
behavior.