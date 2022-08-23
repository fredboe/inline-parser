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
public class ParserTest {
    enum ANNOTATION {
    }

    private final Parser<TYPE, ANNOTATION> exprParser =
            ArithmeticParser.<ANNOTATION>arithmeticExample().getParser("EXPR");

    private final Consumable consumable1 = new Consumable("42 + 11 - 1*20/10-14",
            Consumable.Ignore.IGNORE_WHITESPACE);
    private final AST<TYPE, ANNOTATION> astOfConsumable1 = setupASTofConsumable1();


    private final Consumable consumable2 = new Consumable("11*2+24/2*3 - 9*2",
            Consumable.Ignore.IGNORE_WHITESPACE);
    private final AST<TYPE, ANNOTATION> astOfConsumable2 = setupASTofConsumable2();


    private AST<TYPE, ANNOTATION> setupASTofConsumable1() {
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

    private AST<TYPE, ANNOTATION> setupASTofConsumable2() {
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

    @Test
    public void Test_fst_input_string() {
        var optionalAST = exprParser.applyTo(consumable1);
        assertTrue(optionalAST.isPresent());
        assertEquals(optionalAST.get(), astOfConsumable1);
    }

    @Test
    public void Test_scd_input_string() {
        var optionalAST = exprParser.applyTo(consumable2);
        assertTrue(optionalAST.isPresent());
        assertEquals(optionalAST.get(), astOfConsumable2);
    }
}
