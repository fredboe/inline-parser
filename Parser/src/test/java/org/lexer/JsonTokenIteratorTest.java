package org.lexer;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.lexer.examples.JsonTokenIterator;
import org.lexer.examples.JsonTokenIterator.TYPE;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JsonTokenIteratorTest {
    private void verify(String text, List<Match<TYPE>> tokenList) {
        var tokenIterator = new JsonTokenIterator(text);
        var listIterator = tokenList.iterator();
        while (tokenIterator.hasNext() && listIterator.hasNext()) {
            assertEquals(tokenIterator.next(), listIterator.next());
        }
        assertFalse(tokenIterator.hasNext() || listIterator.hasNext());
    }

    @Test
    public void Test_JsonTokenIterator1() {
        String json =
                """
                {
                    "name": "Fred",
                    "age": 20
                }
                """;
        List<Match<TYPE>> tokenList = List.of(
                new Match<>(TYPE.CURLY_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.CURLY_BRAC_C, null)
        );
        verify(json, tokenList);
    }

    @Test
    public void Test_JsonTokenIterator2() {
        String json =
                """
                {
                    "name": "inline-parser",
                    "num_lines": 1183.1234e-10,
                    "on_github": true,
                    "test": null,
                    "something": ["name", "num_lines", "on_github"]
                }
                """;
        List<Match<TYPE>> tokenList = List.of(
                new Match<>(TYPE.CURLY_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.TRUE, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.NULL, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.SQ_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.SQ_BRAC_C, null),
                new Match<>(TYPE.CURLY_BRAC_C, null)
        );
        verify(json, tokenList);
    }

    @Test
    public void Test_JsonTokenIterator3() {
        String json =
                """
                {
                    "good_movies":
                        [
                            {"name": "The dark knight", "release": 2008},
                            {"name": "The Shawshank Redemption", "release": 1994}
                        ],
                        "best_movie":
                            {"name": "The Godfather", "release": 1972}
                }
                """;
        List<Match<TYPE>> tokenList = List.of(
                new Match<>(TYPE.CURLY_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.SQ_BRAC_O, null),
                new Match<>(TYPE.CURLY_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.CURLY_BRAC_C, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.CURLY_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.CURLY_BRAC_C, null),
                new Match<>(TYPE.SQ_BRAC_C, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.CURLY_BRAC_O, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COMMA, null),
                new Match<>(TYPE.STRING, null),
                new Match<>(TYPE.COLON, null),
                new Match<>(TYPE.NUMBER, null),
                new Match<>(TYPE.CURLY_BRAC_C, null),
                new Match<>(TYPE.CURLY_BRAC_C, null)
        );
        verify(json, tokenList);
    }
}
