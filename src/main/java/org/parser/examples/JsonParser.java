package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.Optional;

public class JsonParser<ANNOTATION> implements Parser<JsonParser.TYPE, ANNOTATION> {
    public enum TYPE {
        ARRAY, OBJECT, PROPERTY, NUMBER, STRING, TRUE, FALSE, NULL
    }

    private final Parser<TYPE, ANNOTATION> jsonParser;

    public JsonParser() {
        jsonParser = JsonParser.<ANNOTATION>jsonExample().getParser("json");
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return jsonParser.applyTo(consumable);
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence,
                Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_LINEBREAK, Consumable.Ignore.IGNORE_COMMENT
        ));
    }

    /**
     * Grammar: <br>
     * json ::= object <br>
     * key_value ::= string ":" value <br>
     * value ::= object | array | string | number | boolean | "null" <br>
     * object ::= "{" key_value ("," key_value)* "}" | "{" "}" <br>
     * array ::= "[" value ("," value)* "]" | "[" "]" <br>
     * string ::= \"[^\"]*\" <br>
     * number ::= (-)?\d+(\.\d*)?((e|E)(+|-)?\d+)? <br>
     * boolean ::= "true" | "false" <br>
     * @return Returns a ParserPool for json strings.
     * @param <ANNOTATION> ANNOTATION type of the AST
     */
    public static <ANNOTATION>ParserPool<TYPE, ANNOTATION> jsonExample() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();

        builder.newRule("key_value")
                .concat(TYPE.PROPERTY).rule("string").match("\\:").rule("value")
                .end();

        builder.newRule("value")
                .rule("object").or()
                .rule("array").or()
                .rule("string").or()
                .rule("number").or()
                .rule("boolean").or()
                .keyword(TYPE.NULL, "null")
                .end();

        builder.newRule("object")
                .concat(TYPE.OBJECT)
                .match("\\{")
                .rule("key_value").many().match(",").rule("key_value").manyEnd()
                .match("\\}")
                .or()
                .concat(TYPE.OBJECT).match("\\{").match("\\}")
                .end();

        builder.newRule("array")
                .concat(TYPE.ARRAY)
                    .match("\\[")
                    .rule("value").many().match(",").rule("value").manyEnd()
                    .match("\\]")
                .or()
                .concat(TYPE.ARRAY).match("\\[").match("\\]")
                .end();

        // first " then any character other than " then ".
        builder.newRule("string")
                .match(TYPE.STRING, "\"[^\"]*\"")
                .end();

        // optional - then some digits, then optional . with digits and then optional exponent starting with e or E, optional +/- and then some digits
        builder.newRule("number")
                .match(TYPE.NUMBER, "(\\-)?\\d+(\\.\\d*)?((e|E)(\\+|\\-)?\\d+)?")
                .end();

        builder.newRule("boolean")
                .keyword(TYPE.TRUE, "true").or().keyword(TYPE.FALSE, "false")
                .end();

        builder.newRule("json").rule("object").end();

        return builder.build();
    }
}
