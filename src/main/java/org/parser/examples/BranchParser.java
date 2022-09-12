package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.base.build.Simplerule;
import org.parser.tree.AST;

import java.util.Optional;

public class BranchParser implements Parser<BranchParser.TYPE> {
    public enum TYPE {
        IF, LEQ, GEQ,
        NUMBER, ADD,
        IDENTIFIER, ASSIGN, BLOCK
    }

    private final Parser<TYPE> branchParser;

    public BranchParser() {
        branchParser = BranchParser.ifExample().getParser("BRANCH");
    }

    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        return branchParser.applyTo(consumable);
    }

    @Override
    public Consumable consumableOf(CharSequence sequence) {
        return new Consumable(sequence, Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_COMMENT);
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
     */
    public static ParserPool<TYPE> ifExample() {
        ParserBuilder<TYPE> builder = new ParserBuilder<>();

        builder.newRule("NUMBER").match(TYPE.NUMBER, "\\d+").end();

        builder.newRule("IDENTIFIER").match(TYPE.IDENTIFIER, "[a-zA-Z]\\w*").end();

        builder.newRule("LITERAL").rule("NUMBER").or().rule("IDENTIFIER").end();

        builder.newRule("IF")
                .type(TYPE.IF).hide("if").hide("\\(").rule("CONDITION").hide("\\)").rule("BLOCK")
                .end();

        builder.newRule("CONDITION")
                .type(TYPE.LEQ).rule("LITERAL").hide("<=").rule("LITERAL")
                .or()
                .type(TYPE.GEQ).rule("LITERAL").hide(">=").rule("LITERAL")
                .end();

        builder.newRule("BLOCK")
                .type(TYPE.BLOCK).hide("\\{").many("ASSIGN").hide("\\}")
                .end();

        builder.newRule("ASSIGN")
                .type(TYPE.ASSIGN).rule("IDENTIFIER").hide("=").rule("EXPR").hide(";")
                .end();

        builder.newRule("EXPR")
                .type(TYPE.ADD).rule("LITERAL").some(new Simplerule<TYPE>().hide("\\+").rule("LITERAL"))
                .or()
                .rule("LITERAL")
                .end();

        builder.newRule("BRANCH").rule("IF").end();

        return builder.build();
    }
}
