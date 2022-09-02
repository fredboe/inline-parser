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
    enum ANNOTATION {}

    private final Parser<TYPE, ANNOTATION> exprParser = new ArithmeticParser<>();

    private AST<TYPE, ANNOTATION> setupASTofExpr1() {
        // 42 + 11 - 1*20/10-14
        AST<TYPE, ANNOTATION> num42 = new AST<>(TYPE.NUMBER, new Consumable.Match("42"));
        AST<TYPE, ANNOTATION> num11 = new AST<>(TYPE.NUMBER, new Consumable.Match("11"));
        AST<TYPE, ANNOTATION> num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        AST<TYPE, ANNOTATION> num20 = new AST<>(TYPE.NUMBER, new Consumable.Match("20"));
        AST<TYPE, ANNOTATION> num10 = new AST<>(TYPE.NUMBER, new Consumable.Match("10"));
        AST<TYPE, ANNOTATION> num14 = new AST<>(TYPE.NUMBER, new Consumable.Match("14"));

        AST<TYPE, ANNOTATION> divAST = new AST<>(TYPE.DIV, null, List.of(num20, num10));
        AST<TYPE, ANNOTATION> mulAST = new AST<>(TYPE.MUL, null, List.of(num1, divAST));
        AST<TYPE, ANNOTATION> subAST = new AST<>(TYPE.SUB, null, List.of(num11, mulAST, num14));

        return new AST<>(TYPE.ADD, null, List.of(num42, subAST));
    }

    private AST<TYPE, ANNOTATION> setupASTofExpr2() {
        // 11*2+24/2*3 - 9*2
        AST<TYPE, ANNOTATION> num11 = new AST<>(TYPE.NUMBER, new Consumable.Match("11"));
        AST<TYPE, ANNOTATION> num2  = new AST<>(TYPE.NUMBER, new Consumable.Match("2"));
        AST<TYPE, ANNOTATION> num24 = new AST<>(TYPE.NUMBER, new Consumable.Match("24"));
        AST<TYPE, ANNOTATION> num3  = new AST<>(TYPE.NUMBER, new Consumable.Match("3"));
        AST<TYPE, ANNOTATION> num9  = new AST<>(TYPE.NUMBER, new Consumable.Match("9"));

        AST<TYPE, ANNOTATION> divAST = new AST<>(TYPE.DIV, null, List.of(num24, num2));
        AST<TYPE, ANNOTATION> mulAST1 = new AST<>(TYPE.MUL, null, List.of(divAST, num3));
        AST<TYPE, ANNOTATION> mulAST2 = new AST<>(TYPE.MUL, null, List.of(num9, num2));
        AST<TYPE, ANNOTATION> subAST = new AST<>(TYPE.SUB, null, List.of(mulAST1, mulAST2));
        AST<TYPE, ANNOTATION> mulAST3 = new AST<>(TYPE.MUL, null, List.of(num11, num2));

        return new AST<>(TYPE.ADD, null, List.of(mulAST3, subAST));
    }

    private AST<TYPE, ANNOTATION> setupASTofExpr3() {
        // sin(14+3)*3^2- 1
        AST<TYPE, ANNOTATION> sin   = new AST<>(TYPE.SIN, new Consumable.Match("sin"));
        AST<TYPE, ANNOTATION> num14 = new AST<>(TYPE.NUMBER, new Consumable.Match("14"));
        AST<TYPE, ANNOTATION> num3  = new AST<>(TYPE.NUMBER, new Consumable.Match("3"));
        AST<TYPE, ANNOTATION> num2  = new AST<>(TYPE.NUMBER, new Consumable.Match("2"));
        AST<TYPE, ANNOTATION> num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));

        AST<TYPE, ANNOTATION> addAST = new AST<>(TYPE.ADD, null, List.of(num14, num3));
        AST<TYPE, ANNOTATION> sinAST = new AST<>(TYPE.FUNC, null, List.of(sin, addAST));
        AST<TYPE, ANNOTATION> potAST = new AST<>(TYPE.POT, null, List.of(num3, num2));
        AST<TYPE, ANNOTATION> mulAST = new AST<>(TYPE.MUL, null, List.of(sinAST, potAST));

        return new AST<>(TYPE.SUB, null, List.of(mulAST, num1));
    }

    private AST<TYPE, ANNOTATION> setupASTofExpr4() {
        // 12 - 11 - (pi - 2^1*2)
        AST<TYPE, ANNOTATION> num12 = new AST<>(TYPE.NUMBER, new Consumable.Match("12"));
        AST<TYPE, ANNOTATION> num11 = new AST<>(TYPE.NUMBER, new Consumable.Match("11"));
        AST<TYPE, ANNOTATION> num2  = new AST<>(TYPE.NUMBER, new Consumable.Match("2"));
        AST<TYPE, ANNOTATION> num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        AST<TYPE, ANNOTATION> pi = new AST<>(TYPE.PI, new Consumable.Match("pi"));

        AST<TYPE, ANNOTATION> potAST = new AST<>(TYPE.POT, null, List.of(num2, num1));
        AST<TYPE, ANNOTATION> mulAST = new AST<>(TYPE.MUL, null, List.of(potAST, num2));
        AST<TYPE, ANNOTATION> subAST = new AST<>(TYPE.SUB, null, List.of(pi, mulAST));

        return new AST<>(TYPE.SUB, null, List.of(num12, num11, subAST));
    }

    private AST<TYPE, ANNOTATION> setupASTofExpr5() {
        // 9 - 5 - 1 + 6 - 5-1
        AST<TYPE, ANNOTATION> num9 = new AST<>(TYPE.NUMBER, new Consumable.Match("9"));
        AST<TYPE, ANNOTATION> num5 = new AST<>(TYPE.NUMBER, new Consumable.Match("5"));
        AST<TYPE, ANNOTATION> num1  = new AST<>(TYPE.NUMBER, new Consumable.Match("1"));
        AST<TYPE, ANNOTATION> num6  = new AST<>(TYPE.NUMBER, new Consumable.Match("6"));

        AST<TYPE, ANNOTATION> subASTl = new AST<>(TYPE.SUB, null, List.of(num9, num5, num1));
        AST<TYPE, ANNOTATION> subASTr = new AST<>(TYPE.SUB, null, List.of(num6, num5, num1));

        return new AST<>(TYPE.ADD, null, List.of(subASTl, subASTr));
    }

    private void testExpr(String expr, AST<TYPE, ANNOTATION> result) {
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
}
