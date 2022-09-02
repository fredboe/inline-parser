# inline-parser
Library for creating parsers in Java code.

### Basics
inline-parser is a library for creating parsers without using a parser generator.
without using a parser generator. So the grammar is specified directly in the code and then
a recursive parser is created from it.
The library tries above all to make a simple readability and implementability possible;
less in the foreground is the possibility to create high-performance parsers.

### Example
In this section, I would like to very briefly introduce the functionality of this
library. If you want to see detailed examples, just have a look at the
`org.parser.examples` package.

As an introductory example to the library, I would like to show how an if-parser
can be created. First, you have to create a `ParserBuilder` object:
```java
ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();
```
Now we can create rules in this builder to represent the grammar.
A rule can be thought of as a line in Backus-Naur form.
If statement consists of `if`, `(`, `condition`, `)` and then `statements`.
Such a rule can be constructed like this:
```java
builder.newRule("IF")
        .concat(TYPE.IF).match("if").match("\\(").rule("CONDITION").match("\\)").rule("BLOCK")
        .end();
```
Now we have to define the rules "CONDITION" and "BLOCK". As an example we show
how the "CONDITION" rule can be created with the relational operators
<= and >=:
```java
builder.newRule("COND")
        .concat(TYPE.LEQ).rule("LITERAL").match("<=").rule("LITERAL")
        .or()
        .concat(TYPE.GEQ).rule("LITERAL").match(">=").rule("LITERAL")
        .end();
```
If you would now create the rules "BLOCK" and "LITERAL", you could create at the end
a `ParserPool` for this grammar as follows:
```java
ParserPool<TYPE, ANNOTATION> pool = builder.build();
```

Where `TYPE` is an enum with the values `IF, LEQ, GEQ, ...` and
`ANNOTATION` is a class (you have to define it yourself), with which you can write annotations to the nodes in the AST.
Also, the backslashes in the match methods are due to the fact that a Regular Expression must be specified there.

### Notes
- Left-recursive grammars are not possible because they lead to infinite recursion.
- If a child of a concat or many parser has a type of null, this AST will not be
  taken over, but the children of the AST are added to the resulting AST at the correct position.
- As I am not a professional programmer, this library might contain a some bugs.

---
### Author
Frederik Böcker

