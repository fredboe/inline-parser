# alpha notation
To see the grammar for alpha notation, have a look at the bottom of this page. I believe every single 
instruction is pretty much self-explanatory, therefore the instructions won't be described.

## Additions
In addition to the normal alpha notation instruction we have provided the following ones
(these are mainly for outputting results):
- 'mem' to have a view on the whole memory
- 'clear' to delete the whole memory
- exe FILENAME (file extension must be .alpha) to execute a file. The resulting memory is then merged with the current memory.
  If one wants to execute the program line by line, then one just needs to add '-lbl' or '-LineByLine' after the filename.
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

## Examples
### Factorial subroutine
```
a_1 := 6 
call fac 
goto end

// a_0 = a_1!
a_0 := 1 : fac
if a_1 = 0 then goto end_fac : loop
a_0 := a_0 * a_1
a_1 := a_1 - 1
goto loop
return : end_fac
```

### Factorial recursive
```
a_1 := 7
call fac
goto end
          
// factorial - recursive
a_0 := 1 : fac
if a_1 = 0 then goto ret : fac_rec
a_0 := a_0 * a_1
a_1 := a_1 - 1
call fac_rec
return : ret
```

### Digit sum subroutine
```
p(1) := 5313294
call digit_sum
goto end

// p(2) = digit_sum(p(1))
a_2 := p(1) : digit_sum
a_0 := 0
if a_2 = 0 then goto end_digit_sum : loop
a_1 := a_2 % 10
a_2 := a_2 / 10
a_0 := a_0 + a_1
goto loop
p(2) := a_0 : end_digit_sum
return
```