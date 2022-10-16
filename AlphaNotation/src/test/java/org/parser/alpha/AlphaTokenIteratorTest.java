package org.parser.alpha;

import org.junit.jupiter.api.Test;
import org.lexer.Match;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AlphaTokenIteratorTest {
    private void verify(String program, List<Match<Type>> tokenList) {
        var tokenIterator = new AlphaTokenIterator(program);
        var listIterator = tokenList.iterator();
        while (tokenIterator.hasNext() && listIterator.hasNext()) {
            assertEquals(tokenIterator.next(), listIterator.next());
        }
        assertFalse(tokenIterator.hasNext() || listIterator.hasNext());
    }

    @Test
    public void Test_AlphaTokenIterator1() {
        String program =
                """
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
                """;
        List<Match<Type>> tokenList = new ArrayList<>();
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.ASSIGN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.CALL, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.GOTO, null));
        tokenList.add(new Match<>(Type.END, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.ASSIGN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.COLON, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.IF_TOKEN, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.EQ, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.THEN_TOKEN, null));
        tokenList.add(new Match<>(Type.GOTO, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.COLON, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.ASSIGN, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.MUL, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.ASSIGN, null));
        tokenList.add(new Match<>(Type.ALPHA_TOKEN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.SUB, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.GOTO, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.RETURN, null));
        tokenList.add(new Match<>(Type.COLON, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));

        verify(program, tokenList);
    }

    @Test
    public void Test_AlphaTokenIterator2() {
        String program =
                """                 
                p(1) := 5
                call square
                goto end
                                
                p(1) := p(1) * p(1) : square
                return
                """;

        List<Match<Type>> tokenList = new ArrayList<>();
        tokenList.add(new Match<>(Type.RHO_TOKEN, null));
        tokenList.add(new Match<>(Type.BRAC_O, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.BRAC_C, null));
        tokenList.add(new Match<>(Type.ASSIGN, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.CALL, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.GOTO, null));
        tokenList.add(new Match<>(Type.END, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.RHO_TOKEN, null));
        tokenList.add(new Match<>(Type.BRAC_O, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.BRAC_C, null));
        tokenList.add(new Match<>(Type.ASSIGN, null));
        tokenList.add(new Match<>(Type.RHO_TOKEN, null));
        tokenList.add(new Match<>(Type.BRAC_O, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.BRAC_C, null));
        tokenList.add(new Match<>(Type.MUL, null));
        tokenList.add(new Match<>(Type.RHO_TOKEN, null));
        tokenList.add(new Match<>(Type.BRAC_O, null));
        tokenList.add(new Match<>(Type.NUMBER, null));
        tokenList.add(new Match<>(Type.BRAC_C, null));
        tokenList.add(new Match<>(Type.COLON, null));
        tokenList.add(new Match<>(Type.LABEL, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));
        tokenList.add(new Match<>(Type.RETURN, null));
        tokenList.add(new Match<>(Type.LINEBREAK, null));

        verify(program, tokenList);
    }
}
