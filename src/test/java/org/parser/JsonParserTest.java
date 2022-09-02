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
    enum ANNOTATION {}

    private final Parser<TYPE, ANNOTATION> jsonParser = new JsonParser<>();

    private AST<TYPE, ANNOTATION> setupASTOfJsonString1() {
        /*
        {
            "name": "Fred",
            "age": 20
        }
         */
        AST<TYPE, ANNOTATION> name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        AST<TYPE, ANNOTATION> fred = new AST<>(TYPE.STRING, new Consumable.Match("\"Fred\""));
        AST<TYPE, ANNOTATION> age = new AST<>(TYPE.STRING, new Consumable.Match("\"age\""));
        AST<TYPE, ANNOTATION> num20 = new AST<>(TYPE.NUMBER, new Consumable.Match("20"));

        AST<TYPE, ANNOTATION> property1 = new AST<>(TYPE.PROPERTY, null, List.of(name, fred));
        AST<TYPE, ANNOTATION> property2 = new AST<>(TYPE.PROPERTY, null, List.of(age, num20));

        return new AST<>(TYPE.OBJECT, null, List.of(property1, property2));
    }

    private AST<TYPE, ANNOTATION> setupASTOfJsonString2() {
        /*
        {
            "name": "inline-parser",
            "num_lines": 1183.1234e-10,
            "on_github": true,
            "test": null
            "something": ["name", "num_lines", "on_github"]
        }
         */
        AST<TYPE, ANNOTATION> name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        AST<TYPE, ANNOTATION> inline_parser = new AST<>(TYPE.STRING, new Consumable.Match("\"inline-parser\""));
        AST<TYPE, ANNOTATION> lines = new AST<>(TYPE.STRING, new Consumable.Match("\"num_lines\""));
        AST<TYPE, ANNOTATION> numLines = new AST<>(TYPE.NUMBER, new Consumable.Match("1183.1234e-10"));
        AST<TYPE, ANNOTATION> github = new AST<>(TYPE.STRING, new Consumable.Match("\"on_github\""));
        AST<TYPE, ANNOTATION> trueK = new AST<>(TYPE.TRUE, null);
        AST<TYPE, ANNOTATION> test = new AST<>(TYPE.STRING, new Consumable.Match("\"test\""));
        AST<TYPE, ANNOTATION> nullK = new AST<>(TYPE.NULL, null);
        AST<TYPE, ANNOTATION> something = new AST<>(TYPE.STRING, new Consumable.Match("\"something\""));

        AST<TYPE, ANNOTATION> somethingArray = new AST<>(TYPE.ARRAY, null, List.of(name, lines, github));
        AST<TYPE, ANNOTATION> nameProperty = new AST<>(TYPE.PROPERTY, null, List.of(name, inline_parser));
        AST<TYPE, ANNOTATION> numLinesProperty = new AST<>(TYPE.PROPERTY, null, List.of(lines, numLines));
        AST<TYPE, ANNOTATION> onGithubProperty = new AST<>(TYPE.PROPERTY, null, List.of(github, trueK));
        AST<TYPE, ANNOTATION> testProperty = new AST<>(TYPE.PROPERTY, null, List.of(test, nullK));
        AST<TYPE, ANNOTATION> somethingProperty = new AST<>(TYPE.PROPERTY, null, List.of(something, somethingArray));

        return new AST<>(TYPE.OBJECT, null,
                List.of(nameProperty, numLinesProperty, onGithubProperty, testProperty, somethingProperty)
        );
    }

    private AST<TYPE, ANNOTATION> setupASTOfJsonString3() {
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
        AST<TYPE, ANNOTATION> name = new AST<>(TYPE.STRING, new Consumable.Match("\"name\""));
        AST<TYPE, ANNOTATION> release = new AST<>(TYPE.STRING, new Consumable.Match("\"release\""));
        AST<TYPE, ANNOTATION> dknight = new AST<>(TYPE.STRING, new Consumable.Match("\"The dark knight\""));
        AST<TYPE, ANNOTATION> redemp = new AST<>(TYPE.STRING, new Consumable.Match("\"The Shawshank Redemption\""));
        AST<TYPE, ANNOTATION> godfather = new AST<>(TYPE.STRING, new Consumable.Match("\"The Godfather\""));
        AST<TYPE, ANNOTATION> num2008 = new AST<>(TYPE.NUMBER, new Consumable.Match("2008"));
        AST<TYPE, ANNOTATION> num1994 = new AST<>(TYPE.NUMBER, new Consumable.Match("1994"));
        AST<TYPE, ANNOTATION> num1972 = new AST<>(TYPE.NUMBER, new Consumable.Match("1972"));
        AST<TYPE, ANNOTATION> good = new AST<>(TYPE.STRING, new Consumable.Match("\"good_movies\""));
        AST<TYPE, ANNOTATION> best = new AST<>(TYPE.STRING, new Consumable.Match("\"best_movie\""));

        AST<TYPE, ANNOTATION> dknightName = new AST<>(TYPE.PROPERTY, null, List.of(name, dknight));
        AST<TYPE, ANNOTATION> dknightRel  = new AST<>(TYPE.PROPERTY, null, List.of(release, num2008));
        AST<TYPE, ANNOTATION> dknightMovie = new AST<>(TYPE.OBJECT, null, List.of(dknightName, dknightRel));

        AST<TYPE, ANNOTATION> redemptionName = new AST<>(TYPE.PROPERTY, null, List.of(name, redemp));
        AST<TYPE, ANNOTATION> redemptionRel  = new AST<>(TYPE.PROPERTY, null, List.of(release, num1994));
        AST<TYPE, ANNOTATION> redemptionMovie = new AST<>(TYPE.OBJECT, null, List.of(redemptionName, redemptionRel));

        AST<TYPE, ANNOTATION> godfatherName = new AST<>(TYPE.PROPERTY, null, List.of(name, godfather));
        AST<TYPE, ANNOTATION> godfatherRel  = new AST<>(TYPE.PROPERTY, null, List.of(release, num1972));
        AST<TYPE, ANNOTATION> godfatherMovie = new AST<>(TYPE.OBJECT, null, List.of(godfatherName, godfatherRel));

        AST<TYPE, ANNOTATION> arrayGood = new AST<>(TYPE.ARRAY, null, List.of(dknightMovie, redemptionMovie));
        AST<TYPE, ANNOTATION> goodMovies = new AST<>(TYPE.PROPERTY, null, List.of(good, arrayGood));
        AST<TYPE, ANNOTATION> bestMovie = new AST<>(TYPE.PROPERTY, null, List.of(best, godfatherMovie));

        return new AST<>(TYPE.OBJECT, null, List.of(goodMovies, bestMovie));
    }



    private void testJson(String json, AST<JsonParser.TYPE, ANNOTATION> result) {
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
