package org.parser.alpha;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.parser.base.Parser;
import org.parser.examples.alpha.AlphaNotationParser;
import org.parser.examples.alpha.Type;
import org.parser.tree.AST;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AlphaNotationParserTest {
    private static final Parser<Type> alphaParser = new AlphaNotationParser();

    private AST<Type> setupProgram1() {
        /*
        a_1 := 6
        call fac
        goto end

        a_0 := 1 : fac
        if a_1 = 0 then goto end_fac : loop
        a_0 := a_0 * a_1
        a_1 := a_1 - 1
        goto loop
        return : end_fac
         */

        var acc0 = new AST<>(Type.ACCUMULATOR, "0");
        var acc1 = new AST<>(Type.ACCUMULATOR, "1");

        var num0 = new AST<>(Type.NUMBER, "0");
        var num1 = new AST<>(Type.NUMBER, "1");
        var num6 = new AST<>(Type.NUMBER, "6");

        var fac = new AST<>(Type.LABEL, "fac");
        var end_fac = new AST<>(Type.LABEL, "end_fac");
        var loop = new AST<>(Type.LABEL, "loop");

        var eq = new AST<>(Type.EQ);
        var mul = new AST<>(Type.MUL);
        var sub = new AST<>(Type.SUB);
        var return_ = new AST<>(Type.RETURN);


        var line1 = new AST<>(Type.ASSIGN, List.of(acc1, num6));
        var line2 = new AST<>(Type.CALL, List.of(fac));
        var line3 = new AST<>(Type.END);

        var assign1 = new AST<>(Type.ASSIGN, List.of(acc0, num1));
        var line4 = new AST<>(Type.LABELED, List.of(assign1, fac));

        var branch = new AST<>(Type.BRANCH, List.of(
                new AST<>(Type.CONDITION, List.of(acc1, eq, num0)),
                new AST<>(Type.GOTO, List.of(end_fac))
        ));
        var line5 = new AST<>(Type.LABELED, List.of(branch, loop));

        var line6 = new AST<>(Type.ASSIGN, List.of(
                acc0,
                new AST<>(Type.EXPR, List.of(acc0, mul, acc1))
        ));
        var line7 = new AST<>(Type.ASSIGN, List.of(
                acc1,
                new AST<>(Type.EXPR, List.of(acc1, sub, num1))
        ));
        var line8 = new AST<>(Type.GOTO, null, List.of(loop));
        var line9 = new AST<>(Type.LABELED, null, List.of(return_, end_fac));

        return new AST<>(Type.PROGRAM, List.of(line1, line2, line3, line4, line5, line6, line7, line8, line9));
    }

    private AST<Type> setupProgram2() {
        /*
        a_1 := 7
        call fac
        goto end

        // recursive factorial
        a_0 := 1 : fac
        if a_1 = 0 then goto ret : fac_rec
        a_0 := a_0 * a_1
        a_1 := a_1 - 1
        call fac_rec
        return : ret
         */

        var acc0 = new AST<>(Type.ACCUMULATOR, "0");
        var acc1 = new AST<>(Type.ACCUMULATOR, "1");

        var num0 = new AST<>(Type.NUMBER, "0");
        var num1 = new AST<>(Type.NUMBER, "1");
        var num7 = new AST<>(Type.NUMBER, "7");

        var fac = new AST<>(Type.LABEL, "fac");
        var fac_rec = new AST<>(Type.LABEL, "fac_rec");
        var ret = new AST<>(Type.LABEL, "ret");

        var eq = new AST<>(Type.EQ);
        var mul = new AST<>(Type.MUL);
        var sub = new AST<>(Type.SUB);

        var line1 = new AST<>(Type.ASSIGN, List.of(acc1, num7));
        var line2 = new AST<>(Type.CALL, List.of(fac));
        var line3 = new AST<>(Type.END);

        var assign1 = new AST<>(Type.ASSIGN, List.of(acc0, num1));
        var line4 = new AST<>(Type.LABELED, List.of(assign1, fac));

        var branch = new AST<>(Type.BRANCH, List.of(
                new AST<>(Type.CONDITION, List.of(acc1, eq, num0)),
                new AST<>(Type.GOTO, List.of(ret))
        ));
        var line5 = new AST<>(Type.LABELED, List.of(branch, fac_rec));

        var line6 = new AST<>(Type.ASSIGN, List.of(
                acc0,
                new AST<>(Type.EXPR, List.of(acc0, mul, acc1))
        ));
        var line7 = new AST<>(Type.ASSIGN, List.of(
                acc1,
                new AST<>(Type.EXPR, List.of(acc1, sub, num1))
        ));
        var line8 = new AST<>(Type.CALL, List.of(fac_rec));
        var line9 = new AST<>(Type.LABELED, List.of(
                new AST<>(Type.RETURN),
                ret
        ));

        return new AST<>(Type.PROGRAM, List.of(
                line1, line2, line3, line4, line5, line6, line7, line8, line9
        ));
    }

    private AST<Type> setupProgram3() {
        /*
        p(1) := 12
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
         */

        var acc0 = new AST<>(Type.ACCUMULATOR, "0");
        var acc1 = new AST<>(Type.ACCUMULATOR, "1");
        var acc2 = new AST<>(Type.ACCUMULATOR, "2");

        var num0 = new AST<>(Type.NUMBER, "0");
        var num1 = new AST<>(Type.NUMBER, "1");
        var num2 = new AST<>(Type.NUMBER, "2");
        var num10 = new AST<>(Type.NUMBER, "10");
        var num12 = new AST<>(Type.NUMBER, "12");

        var digit_sum = new AST<>(Type.LABEL, "digit_sum");
        var end_digit_sum = new AST<>(Type.LABEL, "end_digit_sum");
        var loop = new AST<>(Type.LABEL, "loop");

        var eq = new AST<>(Type.EQ);
        var mod = new AST<>(Type.MOD);
        var add = new AST<>(Type.ADD);
        var div = new AST<>(Type.DIV);

        var p_1 = new AST<>(Type.ADDRESS, List.of(num1));
        var p_2 = new AST<>(Type.ADDRESS, List.of(num2));

        var line1 = new AST<>(Type.ASSIGN, List.of(p_1, num12));
        var line2 = new AST<>(Type.CALL, List.of(digit_sum));
        var line3 = new AST<>(Type.END);

        var assign1 = new AST<>(Type.ASSIGN, List.of(acc2, p_1));
        var line4 = new AST<>(Type.LABELED, List.of(assign1, digit_sum));

        var line5 = new AST<>(Type.ASSIGN, List.of(acc0, num0));

        var branch = new AST<>(Type.BRANCH, List.of(
                new AST<>(Type.CONDITION, List.of(acc2, eq, num0)),
                new AST<>(Type.GOTO, List.of(end_digit_sum))
        ));
        var line6 = new AST<>(Type.LABELED, List.of(branch, loop));

        var line7 = new AST<>(Type.ASSIGN, List.of(
                acc1,
                new AST<>(Type.EXPR, List.of(acc2, mod, num10))
        ));
        var line8 = new AST<>(Type.ASSIGN, List.of(
                acc2,
                new AST<>(Type.EXPR, List.of(acc2, div, num10))
        ));
        var line9 = new AST<>(Type.ASSIGN, List.of(
                acc0,
                new AST<>(Type.EXPR, List.of(acc0, add, acc1))
        ));
        var line10 = new AST<>(Type.GOTO, List.of(loop));

        var assign2 = new AST<>(Type.ASSIGN, List.of(p_2, acc0));
        var line11 = new AST<>(Type.LABELED, List.of(assign2, end_digit_sum));
        var line12 = new AST<>(Type.RETURN);

        return new AST<>(Type.PROGRAM, List.of(
                line1, line2, line3, line4, line5, line6, line7, line8, line9, line10, line11, line12
        ));
    }

    private AST<Type> setupProgram4() {
        /*
        a_0 := 1
        a_1 := 4
        push a_0
        push a_1 // hello
        push 6
        stack +
        push 7
        stack *
        pop p(42)
         */

        var acc0 = new AST<>(Type.ACCUMULATOR, "0");
        var acc1 = new AST<>(Type.ACCUMULATOR, "1");

        var num1 = new AST<>(Type.NUMBER, "1");
        var num4 = new AST<>(Type.NUMBER, "4");
        var num6 = new AST<>(Type.NUMBER, "6");
        var num7 = new AST<>(Type.NUMBER, "7");
        var num42 = new AST<>(Type.NUMBER, "42");

        var p_42 = new AST<>(Type.ADDRESS, List.of(num42));

        var add = new AST<>(Type.ADD);
        var mul = new AST<>(Type.MUL);

        var line1 = new AST<>(Type.ASSIGN, List.of(acc0, num1));
        var line2 = new AST<>(Type.ASSIGN, List.of(acc1, num4));
        var line3 = new AST<>(Type.PUSH, List.of(acc0));
        var line4 = new AST<>(Type.PUSH, List.of(acc1));
        var line5 = new AST<>(Type.PUSH, List.of(num6));
        var line6 = new AST<>(Type.STACK_OP, List.of(add));
        var line7 = new AST<>(Type.PUSH, List.of(num7));
        var line8 = new AST<>(Type.STACK_OP, List.of(mul));
        var line9 = new AST<>(Type.POP, List.of(p_42));

        return new AST<>(Type.PROGRAM, List.of(
                line1, line2, line3, line4, line5, line6, line7, line8, line9
        ));
    }

    private AST<Type> setupOfProgram5() {
        /*
        p(1) := 42
        p(p(1)) := 7
         */

        var num1 = new AST<>(Type.NUMBER, "1");
        var num7 = new AST<>(Type.NUMBER, "7");
        var num42 = new AST<>(Type.NUMBER, "42");

        var p_1 = new AST<>(Type.ADDRESS, List.of(num1));

        var line1 = new AST<>(Type.ASSIGN, List.of(p_1, num42));
        var line2 = new AST<>(Type.ASSIGN, List.of(
           new AST<>(Type.ADDRESS, List.of(p_1)),
           num7
        ));

        return new AST<>(Type.PROGRAM, List.of(line1, line2));
    }

    private void testProgram(String expr, AST<Type> result) {
        var optionalAST = alphaParser.applyTo(expr);
        assertTrue(optionalAST.isPresent());
        assertEquals(optionalAST.get(), result);
    }

    @Test
    public void Test_program_1() {
        String program =
                """
                a_1 := 6
                call fac
                goto end
                        
                a_0 := 1 : fac
                if a_1 = 0 then goto end_fac : loop
                a_0 := a_0 * a_1
                a_1 := a_1 - 1
                goto loop
                return : end_fac
                """;
        testProgram(program, setupProgram1());
    }

    @Test
    public void Test_program_2() {
        String program =
                """
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
                """;
        testProgram(program, setupProgram2());
    }

    @Test
    public void Test_program_3() {
        String program =
                """
                p(1) := 12
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
                """;
        testProgram(program, setupProgram3());
    }

    @Test
    public void Test_program_4() {
        String program =
                """
                a_0 := 1
                a_1 := 4
                push a_0
                push a_1 // hello
                push 6
                stack +
                push 7
                stack *
                pop p(42)
                """;
        testProgram(program, setupProgram4());
    }

    @Test
    public void Test_program_5() {
        String program =
                """
                p(1) := 42
                p(p(1)) := 7
                """;
        testProgram(program, setupOfProgram5());
    }
}
