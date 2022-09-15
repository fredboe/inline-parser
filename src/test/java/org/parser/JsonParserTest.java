package org.parser;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.parser.base.Parser;
import org.parser.examples.JsonParser;
import org.parser.examples.JsonParser.TYPE;
import org.parser.tree.AST;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JsonParserTest {

    private static final Parser<TYPE> jsonParser = new JsonParser();

    private AST<TYPE> setupASTOfJsonString1() {
        /*
        {
            "name": "Fred",
            "age": 20
        }
         */
        var name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        var fred = new AST<>(TYPE.STRING, new Consumable.Match("\"Fred\""));
        var age = new AST<>(TYPE.STRING, new Consumable.Match("\"age\""));
        var num20 = new AST<>(TYPE.NUMBER, new Consumable.Match("20"));

        var property1 = new AST<>(TYPE.PROPERTY, null, List.of(name, fred));
        var property2 = new AST<>(TYPE.PROPERTY, null, List.of(age, num20));

        return new AST<>(TYPE.OBJECT, null, List.of(property1, property2));
    }

    private AST<TYPE> setupASTOfJsonString2() {
        /*
        {
            "name": "inline-parser",
            "num_lines": 1183.1234e-10,
            "on_github": true,
            "test": null
            "something": ["name", "num_lines", "on_github"]
        }
         */
        var name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        var inline_parser = new AST<>(TYPE.STRING, new Consumable.Match("\"inline-parser\""));
        var lines = new AST<>(TYPE.STRING, new Consumable.Match("\"num_lines\""));
        var numLines = new AST<>(TYPE.NUMBER, new Consumable.Match("1183.1234e-10"));
        var github = new AST<>(TYPE.STRING, new Consumable.Match("\"on_github\""));
        var trueK = new AST<>(TYPE.TRUE);
        var test = new AST<>(TYPE.STRING, new Consumable.Match("\"test\""));
        var nullK = new AST<>(TYPE.NULL);
        var something = new AST<>(TYPE.STRING, new Consumable.Match("\"something\""));

        var somethingArray = new AST<>(TYPE.ARRAY, List.of(name, lines, github));
        var nameProperty = new AST<>(TYPE.PROPERTY, List.of(name, inline_parser));
        var numLinesProperty = new AST<>(TYPE.PROPERTY, List.of(lines, numLines));
        var onGithubProperty = new AST<>(TYPE.PROPERTY, List.of(github, trueK));
        var testProperty = new AST<>(TYPE.PROPERTY, List.of(test, nullK));
        var somethingProperty = new AST<>(TYPE.PROPERTY, List.of(something, somethingArray));

        return new AST<>(TYPE.OBJECT,
                List.of(nameProperty, numLinesProperty, onGithubProperty, testProperty, somethingProperty)
        );
    }

    private AST<TYPE> setupASTOfJsonString3() {
        /*
        {
            "good_movies":
                [
                    {"name": "The dark knight", "release": 2008},
                    {"name": "The Shawshank Redemption", "release: 1994}
                ],
            "best_movie":
                    {"name": "The Godfather", "release": 1972}
         }
         */
        var name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        var release = new AST<>(TYPE.STRING, new Consumable.Match("\"release\""));
        var dknight = new AST<>(TYPE.STRING, new Consumable.Match("\"The dark knight\""));
        var redemp = new AST<>(TYPE.STRING, new Consumable.Match("\"The Shawshank Redemption\""));
        var godfather = new AST<>(TYPE.STRING, new Consumable.Match("\"The Godfather\""));
        var num2008 = new AST<>(TYPE.NUMBER, new Consumable.Match("2008"));
        var num1994 = new AST<>(TYPE.NUMBER, new Consumable.Match("1994"));
        var num1972 = new AST<>(TYPE.NUMBER, new Consumable.Match("1972"));
        var good = new AST<>(TYPE.STRING, new Consumable.Match("\"good_movies\""));
        var best = new AST<>(TYPE.STRING, new Consumable.Match("\"best_movie\""));

        var dknightName = new AST<>(TYPE.PROPERTY, List.of(name, dknight));
        var dknightRel  = new AST<>(TYPE.PROPERTY, List.of(release, num2008));
        var dknightMovie = new AST<>(TYPE.OBJECT, List.of(dknightName, dknightRel));

        var redemptionName = new AST<>(TYPE.PROPERTY, List.of(name, redemp));
        var redemptionRel  = new AST<>(TYPE.PROPERTY, List.of(release, num1994));
        var redemptionMovie = new AST<>(TYPE.OBJECT, List.of(redemptionName, redemptionRel));

        var godfatherName = new AST<>(TYPE.PROPERTY, List.of(name, godfather));
        var godfatherRel  = new AST<>(TYPE.PROPERTY, List.of(release, num1972));
        var godfatherMovie = new AST<>(TYPE.OBJECT, List.of(godfatherName, godfatherRel));

        var arrayGood = new AST<>(TYPE.ARRAY, List.of(dknightMovie, redemptionMovie));
        var goodMovies = new AST<>(TYPE.PROPERTY, List.of(good, arrayGood));
        var bestMovie = new AST<>(TYPE.PROPERTY, List.of(best, godfatherMovie));

        return new AST<>(TYPE.OBJECT, List.of(goodMovies, bestMovie));
    }



    private void testJson(String json, AST<JsonParser.TYPE> result) {
        var optionalAST = jsonParser.applyTo(json);
        assertTrue(optionalAST.isPresent());
        assertEquals(optionalAST.get(), result);
    }

    @Test
    public void Test_json_string1() {
        String json =
                """
                {
                    "name": "Fred",
                    "age": 20
                }
                """;
        testJson(json, setupASTOfJsonString1());
    }

    @Test
    public void Test_json_string2() {
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
        testJson(json, setupASTOfJsonString2());
    }

    @Test
    public void Test_json_string3() {
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
        testJson(json, setupASTOfJsonString3());
    }
}
