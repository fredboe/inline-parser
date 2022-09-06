package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.Optional;

public class BranchParser<ANNOTATION> implements Parser<BranchParser.TYPE, ANNOTATION> {
    public enum TYPE {
        IF, LEQ, GEQ,
        NUMBER, ADD,
        IDENTIFIER, ASSIGN, BLOCK
    }

    private final Parser<TYPE, ANNOTATION> branchParser;

    public BranchParser() {
        branchParser = BranchParser.<ANNOTATION>ifExample().getParser("BRANCH");
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return branchParser.applyTo(consumable);
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence,
                Consumable.Ignore.IGNORE_LINEBREAK, Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_COMMENT)
        );
    }

    /**
     * Grammar: <br>
     * branch ::= if
     * number ::= \d+ <br>
     * identifier ::= [a-zA-Z]\w* <br>
     * literal ::= number | identifier <br>
     * if ::= "if" "(" condition ")" block <br>
     * condition ::= literal "<=" literal | literal ">=" literal <br>
     * block ::= "{" (block)* "}" <br>
     * assign ::= identifier "=" expr <br>
     * expr ::= literal ("+" literal)+ | literal <br>
     *
     * @return Returns a ParserPool for simplified If expressions.
     * @param <ANNOTATION> ANNOTATION type of the AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> ifExample() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();

        builder.newRule("NUMBER").match(TYPE.NUMBER, "\\d+").end();

        builder.newRule("IDENTIFIER")
                .match(TYPE.IDENTIFIER, "[a-zA-Z]\\w*")
                .end();

        builder.newRule("LITERAL")
                .rule("NUMBER").or().rule("IDENTIFIER")
                .end();

        builder.newRule("IF")
                .concat(TYPE.IF).hide("if").hide("\\(").rule("CONDITION").hide("\\)").rule("BLOCK")
                .end();

        builder.newRule("CONDITION")
                .concat(TYPE.LEQ).rule("LITERAL").hide("<=").rule("LITERAL")
                .or()
                .concat(TYPE.GEQ).rule("LITERAL").hide(">=").rule("LITERAL")
                .end();

        builder.newRule("BLOCK")
                .concat(TYPE.BLOCK).hide("\\{").many("ASSIGN").hide("\\}")
                .end();

        builder.newRule("ASSIGN")
                .concat(TYPE.ASSIGN).rule("IDENTIFIER").hide("=").rule("EXPR").hide(";")
                .end();

        builder.newRule("EXPR")
                .concat(TYPE.ADD).rule("LITERAL").some().match("\\+").rule("LITERAL").someEnd()
                .or()
                .rule("LITERAL")
                .end();

        builder.newRule("BRANCH").rule("IF").end();

        return builder.build();
    }
}
