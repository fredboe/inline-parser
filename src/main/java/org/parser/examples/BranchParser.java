package org.parser.examples;

import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;

public class BranchParser {
    public enum TYPE {
        IF, LEQ, GEQ,
        NUMBER, ADD,
        IDENTIFIER, ASSIGN, BLOCK
    }

    /**
     * Grammar: <br>
     * number ::= [0-9] number | [0-9] <br>
     * identifier ::= [a-zA-Z_0-9] identifier | [a-zA-Z_0-9] <br>
     * literal ::= number | identifier <br>
     * if ::= "if" "(" condition ")" block <br>
     * condition ::= literal "<=" literal | literal ">=" literal <br>
     * block ::= "{" block2 "}" | "{" "}" <br>
     * block2 ::= assign block2 | assign <br>
     * assign ::= identifier "=" expr <br>
     * expr ::= literal "+" expr | literal <br>
     *
     * @return Returns a ParserPool for simplified If expressions.
     * @param <ANNOTATION> ANNOTATION type of the AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> ifExample() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();

        builder.newRule("NUMBER").consistsOf().match(TYPE.NUMBER, "\\d+").end();

        builder.newRule("IDENTIFIER").consistsOf()
                .match(TYPE.IDENTIFIER, "[a-zA-Z]\\w*")
                .end();

        builder.newRule("LITERAL").consistsOf()
                .rule("NUMBER").or().rule("IDENTIFIER")
                .end();

        builder.newRule("IF").consistsOf()
                .concat(TYPE.IF).match("if").match("\\(").rule("CONDITION").match("\\)").rule("BLOCK")
                .end();

        builder.newRule("CONDITION").consistsOf()
                .concat(TYPE.LEQ).rule("LITERAL").match("<=").rule("LITERAL")
                .or()
                .concat(TYPE.GEQ).rule("LITERAL").match(">=").rule("LITERAL")
                .end();

        builder.newRule("BLOCK").consistsOf()
                .concat(TYPE.BLOCK).match("\\{").many("ASSIGN").match("\\}")
                .end();

        builder.newRule("ASSIGN").consistsOf()
                .concat(TYPE.ASSIGN).rule("IDENTIFIER").match("=").rule("EXPR").match(";")
                .end();

        builder.newRule("EXPR").consistsOf()
                .concat(TYPE.ADD).rule("LITERAL").match("\\+").rule("EXPR")
                .or()
                .rule("LITERAL")
                .end();

        return builder.build();
    }
}
