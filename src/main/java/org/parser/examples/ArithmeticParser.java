package org.parser.examples;

import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;

public class ArithmeticParser {
    public enum TYPE {
        NUMBER, ADD, SUB, MUL, DIV
    }

    /**
     * Grammar: <br>
     * number ::= [0-9] number | [0-9] <br>
     * factor ::= "(" expr ")" | number <br>
     * term ::= factor "*" term | factor "/" term | number <br>
     * expr ::= term "+" expr | term "-" expr | term <br>
     *
     * @return Gibt einen ParserPool für arithmetische Ausdrücke zurück
     * @param <ANNOTATION> ANNOTATION-Typ des AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> arithmeticExample() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();
        builder.newRule("NUMBER").consistsOf().match(TYPE.NUMBER, "\\d+").end();

        builder.newRule("FACTOR").consistsOf()
                .concat().match("\\(").rule("EXPR").match("\\)")
                .or()
                .rule("NUMBER")
                .end();

        builder.newRule("TERM").consistsOf()
                .concat(TYPE.MUL).rule("FACTOR").match("\\*").rule("TERM")
                .or()
                .concat(TYPE.DIV).rule("FACTOR").match("/").rule("TERM")
                .or()
                .rule("FACTOR")
                .end();

        builder.newRule("EXPR").consistsOf()
                .concat(TYPE.ADD).rule("TERM").match("\\+").rule("EXPR")
                .or()
                .concat(TYPE.SUB).rule("TERM").match("\\-").rule("EXPR")
                .or()
                .rule("TERM")
                .end();

        return builder.build();
    }
}
