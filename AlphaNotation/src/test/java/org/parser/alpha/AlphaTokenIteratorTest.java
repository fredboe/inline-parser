package org.parser.alpha;

import org.junit.jupiter.api.Test;
import org.lexer.Match;

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
        List<Match<Type>> tokenList = List.of(

        );
    }
}
