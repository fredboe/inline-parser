package org.parser;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.parser.base.Parser;
import org.parser.examples.ArithmeticParser;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ParserTest {
    enum ANNOTATION {
    }

    private final Parser<ArithmeticParser.TYPE, ANNOTATION> exprParser =
            ArithmeticParser.<ANNOTATION>arithmeticExample().getParser("EXPR");

    private final Consumable consumable1 = new Consumable("42 + 11 - 1*20/10-14",
            Consumable.Ignore.IGNORE_WHITESPACE);

    private final Consumable consumable2 = new Consumable("11*2+24/2*3 - 9*2",
            Consumable.Ignore.IGNORE_WHITESPACE);


    @Test
    public void Test_fst_input_string() {

    }

    @Test
    public void Test_scd_input_string() {

    }
}
