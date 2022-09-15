# alpha notation
To see the grammar for alpha notation, have a look at the bottom of this page. I believe every single 
instruction is pretty much self-explanatory, therefore the instructions won't be described.

## Additions
In addition to the normal alpha notation instruction we have provided the following ones
(these are mainly for outputting results):
- 'mem' to have a view on the whole memory
- 'clear' to delete the whole memory
- exe FILE to execute a file. The resulting memory is then merged with the current memory.
- Just enter a VALUE (memory, register, constant or label) to let it print on the console.

## Grammar
```
   PROGRAM ::= (UNIT ENDL)* <br>
   UNIT ::= LINE ":" LABEL | LINE <br>
   LINE ::= BRANCH | GOTO | ASSIGN | FUNC | STACK | OUTPUT <br>
   BRANCH ::= "if" "(" CONDITION ")" GOTO <br>
   CONDITION ::= VALUE COMP_OPERATOR VALUE <br>
   GOTO ::= "goto" VALUE <br>
   VALUE ::= NUMBER | ACCUMULATOR | ADDRESS <br>
   ASSIGNABLE ::= ACCUMULATOR | ADDRESS <br>
   ASSIGN ::= ASSIGNABLE ":=" EXPR <br>
   EXPR ::= VALUE OPERATOR VALUE <br>
   FUNC ::= "call" VALUE | "return" <br>
   STACK ::= "push" VALUE | "pop" VALUE | "stack_op" OPERATOR <br>
   ACCUMULATOR ::= a_\d+ <br>
   ADDRESS ::= "p" "(" VALUE ")" <br>
   NUMBER ::= (\-)?\d+ <br>
   LABEL ::= [a-zA-Z]\w* <br>
   OPERATOR ::= "+" | "-" | "*" | "/" | "%" <br>
   COMP_OPERATOR ::= "<=" | ">=" | "<" | ">" | "=" <br>
   OUTPUT ::= "mem" | "clear" | EXE | PRINT <br>
   EXE ::= "exe" filename <br>
   PRINT ::= VALUE
   ENDL ::= "\R" <br>
```