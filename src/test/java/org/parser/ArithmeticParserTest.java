package org.parser;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.parser.base.Parser;
import org.parser.examples.ArithmeticParser;
import org.parser.tree.AST;
import org.parser.examples.ArithmeticParser.TYPE;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ArithmeticParserTest {

    private static final Parser<TYPE> exprParser = new ArithmeticParser();

    private AST<TYPE> setupASTofExpr1() {
        // 42 + 11 - 1*20/10-14
        var num42 = new AST<>(TYPE.NUMBER, new Consumable.Match("42"));
        var num11 = new AST<>(TYPE.NUMBER, new Consumable.Match("11"));
        var num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        var num20 = new AST<>(TYPE.NUMBER, new Consumable.Match("20"));
        var num10 = new AST<>(TYPE.NUMBER, new Consumable.Match("10"));
        var num14 = new AST<>(TYPE.NUMBER, new Consumable.Match("14"));

        var divAST = new AST<>(TYPE.DIV, List.of(num20, num10));
        var mulAST = new AST<>(TYPE.MUL, List.of(num1, divAST));
        var subAST = new AST<>(TYPE.SUB, List.of(num11, mulAST, num14));

        return new AST<>(TYPE.ADD, List.of(num42, subAST));
    }

    private AST<TYPE> setupASTofExpr2() {
        // 11*2+24/2*3 - 9*2
        var num11 = new AST<>(TYPE.NUMBER, new Consumable.Match("11"));
        var num2  = new AST<>(TYPE.NUMBER, new Consumable.Match("2"));
        var num24 = new AST<>(TYPE.NUMBER, new Consumable.Match("24"));
        var num3  = new AST<>(TYPE.NUMBER, new Consumable.Match("3"));
        var num9  = new AST<>(TYPE.NUMBER, new Consumable.Match("9"));

        var divAST = new AST<>(TYPE.DIV, List.of(num24, num2));
        var mulAST1 = new AST<>(TYPE.MUL, List.of(divAST, num3));
        var mulAST2 = new AST<>(TYPE.MUL, List.of(num9, num2));
        var subAST = new AST<>(TYPE.SUB, List.of(mulAST1, mulAST2));
        var mulAST3 = new AST<>(TYPE.MUL, List.of(num11, num2));

        return new AST<>(TYPE.ADD, List.of(mulAST3, subAST));
    }

    private AST<TYPE> setupASTofExpr3() {
        // sin(14+3)*3^2- 1
        var sin   = new AST<>(TYPE.SIN, new Consumable.Match("sin"));
        var num14 = new AST<>(TYPE.NUMBER, new Consumable.Match("14"));
        var num3  = new AST<>(TYPE.NUMBER, new Consumable.Match("3"));
        var num2  = new AST<>(TYPE.NUMBER, new Consumable.Match("2"));
        var num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));

        var addAST = new AST<>(TYPE.ADD, List.of(num14, num3));
        var sinAST = new AST<>(TYPE.FUNC, List.of(sin, addAST));
        var potAST = new AST<>(TYPE.POT, List.of(num3, num2));
        var mulAST = new AST<>(TYPE.MUL, List.of(sinAST, potAST));

        return new AST<>(TYPE.SUB, List.of(mulAST, num1));
    }

    private AST<TYPE> setupASTofExpr4() {
        // 12 - 11 - (pi - 2^1*2)
        var num12 = new AST<>(TYPE.NUMBER, new Consumable.Match("12"));
        var num11 = new AST<>(TYPE.NUMBER, new Consumable.Match("11"));
        var num2  = new AST<>(TYPE.NUMBER, new Consumable.Match("2"));
        var num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        var pi = new AST<>(TYPE.PI, new Consumable.Match("pi"));

        var potAST = new AST<>(TYPE.POT, List.of(num2, num1));
        var mulAST = new AST<>(TYPE.MUL, List.of(potAST, num2));
        var subAST = new AST<>(TYPE.SUB, List.of(pi, mulAST));

        return new AST<>(TYPE.SUB, List.of(num12, num11, subAST));
    }

    private AST<TYPE> setupASTofExpr5() {
        // 9 - 5 - 1 + 6 - 5-1
        var num9 = new AST<>(TYPE.NUMBER, new Consumable.Match("9"));
        var num5 = new AST<>(TYPE.NUMBER, new Consumable.Match("5"));
        var num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        var num6  = new AST<>(TYPE.NUMBER, new Consumable.Match("6"));

        var subASTl = new AST<>(TYPE.SUB, List.of(num9, num5, num1));
        var subASTr = new AST<>(TYPE.SUB, List.of(num6, num5, num1));

        return new AST<>(TYPE.ADD, List.of(subASTl, subASTr));
    }

    private AST<TYPE> setupASTofExpr6() {
        // 1 + 1--
        var num1 = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        return new AST<>(TYPE.ADD, List.of(num1, num1));
    }

    private void testExpr(String expr, AST<TYPE> result) {
        var optionalAST = exprParser.applyTo(expr);
        assertTrue(optionalAST.isPresent());
        assertEquals(optionalAST.get(), result);
    }

    @Test
    public void Test_expr1_string() {
        String expr = "42 + 11 - 1*20/10-14";
        testExpr(expr, setupASTofExpr1());
    }

    @Test
    public void Test_expr2_string() {
        String expr = "11*2+24/2*3 - 9*2";
        testExpr(expr, setupASTofExpr2());
    }

    @Test
    public void Test_expr3_string() {
        String expr = "sin(14+3)*3^2- 1";
        testExpr(expr, setupASTofExpr3());
    }

    @Test
    public void Test_expr4_string() {
        String expr = "12 - 11 - (pi - 2^1*2)";
        testExpr(expr, setupASTofExpr4());
    }

    @Test
    public void Test_expr5_string() {
        String expr = "9 - 5 - 1 + 6 - 5-1";
        testExpr(expr, setupASTofExpr5());
    }

    @Test
    public void Test_expr6_string() {
        String expr = "1 + 1--";
        testExpr(expr, setupASTofExpr6());
    }
}
