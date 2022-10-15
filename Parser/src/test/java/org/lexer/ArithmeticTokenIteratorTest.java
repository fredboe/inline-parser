package org.lexer;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.lexer.examples.ArithmeticTokenIterator;
import org.lexer.examples.ArithmeticTokenIterator.TYPE;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ArithmeticTokenIteratorTest {
    private void verify(String expr, List<Match<TYPE>> tokenList) {
        var tokenIterator = new ArithmeticTokenIterator(expr);
        var listIterator = tokenList.iterator();
        while (tokenIterator.hasNext() && listIterator.hasNext()) {
            assertEquals(tokenIterator.next(), listIterator.next());
        }
        assertFalse(tokenIterator.hasNext() || listIterator.hasNext());
    }

    @Test
    public void Test_ArithmeticTokenIterator1() {
        String expr = "42 + 11 - 1*20/10-14";
        List<Match<TYPE>> tokenList = List.of(
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.ADD, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.SUB, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.MUL, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.DIV, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.SUB, null),
                new Match<>(TYPE.NUMBER, null)
        );
        verify(expr, tokenList);
    }

    @Test
    public void Test_ArithmeticTokenIterator2() {
        String expr = "sin(14+3)*3^2 -1";
        List<Match<TYPE>> tokenList = List.of(
                new Match<>(TYPE.SIN, null),
                new Match<>(TYPE.BRAC_O, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.ADD, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.BRAC_C, null),
                new Match<>(TYPE.MUL, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.POT, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.SUB, null),
                new Match<>(TYPE.NUMBER, null)
        );
        verify(expr, tokenList);
    }
}
