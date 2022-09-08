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

    private final Parser<TYPE> jsonParser = new JsonParser();

    private AST<TYPE> setupASTOfJsonString1() {
        /*
        {
            "name": "Fred",
            "age": 20
        }
         */
        AST<TYPE> name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        AST<TYPE> fred = new AST<>(TYPE.STRING, new Consumable.Match("\"Fred\""));
        AST<TYPE> age = new AST<>(TYPE.STRING, new Consumable.Match("\"age\""));
        AST<TYPE> num20 = new AST<>(TYPE.NUMBER, new Consumable.Match("20"));

        AST<TYPE> property1 = new AST<>(TYPE.PROPERTY, null, List.of(name, fred));
        AST<TYPE> property2 = new AST<>(TYPE.PROPERTY, null, List.of(age, num20));

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
        AST<TYPE> name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        AST<TYPE> inline_parser = new AST<>(TYPE.STRING, new Consumable.Match("\"inline-parser\""));
        AST<TYPE> lines = new AST<>(TYPE.STRING, new Consumable.Match("\"num_lines\""));
        AST<TYPE> numLines = new AST<>(TYPE.NUMBER, new Consumable.Match("1183.1234e-10"));
        AST<TYPE> github = new AST<>(TYPE.STRING, new Consumable.Match("\"on_github\""));
        AST<TYPE> trueK = new AST<>(TYPE.TRUE, null);
        AST<TYPE> test = new AST<>(TYPE.STRING, new Consumable.Match("\"test\""));
        AST<TYPE> nullK = new AST<>(TYPE.NULL, null);
        AST<TYPE> something = new AST<>(TYPE.STRING, new Consumable.Match("\"something\""));

        AST<TYPE> somethingArray = new AST<>(TYPE.ARRAY, null, List.of(name, lines, github));
        AST<TYPE> nameProperty = new AST<>(TYPE.PROPERTY, null, List.of(name, inline_parser));
        AST<TYPE> numLinesProperty = new AST<>(TYPE.PROPERTY, null, List.of(lines, numLines));
        AST<TYPE> onGithubProperty = new AST<>(TYPE.PROPERTY, null, List.of(github, trueK));
        AST<TYPE> testProperty = new AST<>(TYPE.PROPERTY, null, List.of(test, nullK));
        AST<TYPE> somethingProperty = new AST<>(TYPE.PROPERTY, null, List.of(something, somethingArray));

        return new AST<>(TYPE.OBJECT, null,
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
        AST<TYPE> name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        AST<TYPE> release = new AST<>(TYPE.STRING, new Consumable.Match("\"release\""));
        AST<TYPE> dknight = new AST<>(TYPE.STRING, new Consumable.Match("\"The dark knight\""));
        AST<TYPE> redemp = new AST<>(TYPE.STRING, new Consumable.Match("\"The Shawshank Redemption\""));
        AST<TYPE> godfather = new AST<>(TYPE.STRING, new Consumable.Match("\"The Godfather\""));
        AST<TYPE> num2008 = new AST<>(TYPE.NUMBER, new Consumable.Match("2008"));
        AST<TYPE> num1994 = new AST<>(TYPE.NUMBER, new Consumable.Match("1994"));
        AST<TYPE> num1972 = new AST<>(TYPE.NUMBER, new Consumable.Match("1972"));
        AST<TYPE> good = new AST<>(TYPE.STRING, new Consumable.Match("\"good_movies\""));
        AST<TYPE> best = new AST<>(TYPE.STRING, new Consumable.Match("\"best_movie\""));

        AST<TYPE> dknightName = new AST<>(TYPE.PROPERTY, null, List.of(name, dknight));
        AST<TYPE> dknightRel  = new AST<>(TYPE.PROPERTY, null, List.of(release, num2008));
        AST<TYPE> dknightMovie = new AST<>(TYPE.OBJECT, null, List.of(dknightName, dknightRel));

        AST<TYPE> redemptionName = new AST<>(TYPE.PROPERTY, null, List.of(name, redemp));
        AST<TYPE> redemptionRel  = new AST<>(TYPE.PROPERTY, null, List.of(release, num1994));
        AST<TYPE> redemptionMovie = new AST<>(TYPE.OBJECT, null, List.of(redemptionName, redemptionRel));

        AST<TYPE> godfatherName = new AST<>(TYPE.PROPERTY, null, List.of(name, godfather));
        AST<TYPE> godfatherRel  = new AST<>(TYPE.PROPERTY, null, List.of(release, num1972));
        AST<TYPE> godfatherMovie = new AST<>(TYPE.OBJECT, null, List.of(godfatherName, godfatherRel));

        AST<TYPE> arrayGood = new AST<>(TYPE.ARRAY, null, List.of(dknightMovie, redemptionMovie));
        AST<TYPE> goodMovies = new AST<>(TYPE.PROPERTY, null, List.of(good, arrayGood));
        AST<TYPE> bestMovie = new AST<>(TYPE.PROPERTY, null, List.of(best, godfatherMovie));

        return new AST<>(TYPE.OBJECT, null, List.of(goodMovies, bestMovie));
    }



    private void testJson(String json, AST<JsonParser.TYPE> result) {
        var optionalAST = jsonParser.applyTo(json);
        assertTrue(optionalAST.isPresent());
        assertEquals(optionalAST.get(), result);
    }

    @Test
    public void Test_json_string1() {
        String json = "{ \"name\": \"Fred\", \"age\": 20 }";
        testJson(json, setupASTOfJsonString1());
    }

    @Test
    public void Test_json_string2() {
        String json =
                "{ " +
                    "\"name\": \"inline-parser\", " +
                    "\"num_lines\": 1183.1234e-10, " +
                    "\"on_github\": true, " +
                    "\"test\": null," +
                    "\"something\": [\"name\", \"num_lines\", \"on_github\"]\n " +
                "}";
        testJson(json, setupASTOfJsonString2());
    }

    @Test
    public void Test_json_string3() {
        String json =
                "{ " +
                    "\"good_movies\": " +
                        "[ " +
                        "{\"name\": \"The dark knight\", \"release\": 2008}, " +
                        "{\"name\": \"The Shawshank Redemption\", \"release\": 1994}" +
                        "]," +
                    "\"best_movie\":\n {\"name\": \"The Godfather\", \"release\": 1972}" +
                "}";
        testJson(json, setupASTOfJsonString3());
    }
}
