package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Environment;
import org.parser.base.Parser;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.base.build.Simplerule;
import org.parser.tree.AST;

import java.util.Optional;

public class JsonParser implements Parser<JsonParser.TYPE> {
    public enum TYPE {
        ARRAY, OBJECT, PROPERTY, NUMBER, STRING, TRUE, FALSE, NULL
    }

    private final Parser<TYPE> jsonParser;

    public JsonParser() {
        jsonParser = JsonParser.jsonExample().getParser("json");
    }

    @Override
    public void processWith(Environment<TYPE> environment) {
        jsonParser.processWith(environment);
    }

    @Override
    public Optional<AST<TYPE>> parse(Consumable consumable) {
        return jsonParser.parse(consumable);
    }

    @Override
    public Consumable consumableOf(CharSequence sequence) {
        return new Consumable(sequence, Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_COMMENT);
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
     */
    public static ParserPool<TYPE> jsonExample() {
        ParserBuilder<TYPE> builder = new ParserBuilder<>();

        builder.newRule("key_value")
                .type(TYPE.PROPERTY).rule("string").hide("\\:").rule("value")
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
                .type(TYPE.OBJECT)
                    .hide("\\{")
                    .rule("key_value").many(new Simplerule<TYPE>().hide(",").rule("key_value"))
                    .hide("\\}")
                .or()
                .type(TYPE.OBJECT).hide("\\{").hide("\\}")
                .end();

        builder.newRule("array")
                .type(TYPE.ARRAY)
                    .hide("\\[")
                    .rule("value").many(new Simplerule<TYPE>().hide(",").rule("value"))
                    .hide("\\]")
                .or()
                .type(TYPE.ARRAY).hide("\\[").hide("\\]")
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

        builder.newRule("json").rule("object").or().rule("array").end();

        return builder.build();
    }
}
