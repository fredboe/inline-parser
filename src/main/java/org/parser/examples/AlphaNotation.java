package org.parser.examples;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.Optional;

public class AlphaNotation<ANNOTATION> implements Parser<AlphaNotation.TYPE, ANNOTATION> {
    public enum TYPE {
        NUMBER, LABEL, ADDRESS, ACCUMULATOR, ASSIGN, GOTO, BRANCH, PROGRAM, LABELED, CONDITION, EXPR,
        CALL, RETURN, PUSH, POP, STACK_OP,
        ADD, SUB, MUL, DIV, MOD,
        LEQ, GEQ, LE, GE, EQ
    }

    private final Parser<TYPE, ANNOTATION> alphaParser;

    public AlphaNotation() {
        alphaParser = AlphaNotation.<ANNOTATION>alphaNotationParser().getParser("PROGRAM");
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        return alphaParser.applyTo(consumable);
    }

    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return alphaParser.applyTo(new Consumable(sequence,
                Consumable.Ignore.IGNORE_WHITESPACE, Consumable.Ignore.IGNORE_LINEBREAK, Consumable.Ignore.IGNORE_COMMENT)
        );
    }

    /**
     * PROGRAM ::= (UNIT ";")* <br>
     * UNIT ::= LINE ":" LABEL | LINE <br>
     * LINE ::= BRANCH | GOTO | STACK | ASSIGN <br>
     * BRANCH ::= "if" CONDITION "then" GOTO <br>
     * CONDITION ::= OPERAND REL_OP OPERAND <br>
     * GOTO ::= "goto" CONSTANT <br>
     * STACK ::= "call" CONSTANT | "return" | "push" OPERAND | pop ACCUMULATOR | "stack" OPERATOR <br>
     * ASSIGN ::= ASSIGNABLE ":=" EXPR <br>
     * EXPR ::= OPERAND OPERATOR OPERAND | OPERAND <br>
     * OPERAND ::= ASSIGNABLE | CONSTANT <br>
     * ASSIGNABLE ::= ADDRESS | ACCUMULATOR <br>
     * CONSTANT ::= LABEL | NUMBER <br>
     * ADDRESS ::= "p" "(" OPERAND ")" <br>
     * ACCUMULATOR ::= "a_" \d+ <br>
     * LABEL ::= [a-zA-Z]\w* <br>
     * NUMBER ::= (-)?\d+ <br>
     * OPERATOR ::= "+" | "-" | "*" | "/" | "%" <br>
     * REL_OP ::= ">=" | "<=" | "<" | ">" | "=" <br>
     * @return Returns a ParserPool for arithmetic expressions.
     * @param <ANNOTATION> ANNOTATION type of the AST
     */
    public static <ANNOTATION> ParserPool<TYPE, ANNOTATION> alphaNotationParser() {
        ParserBuilder<TYPE, ANNOTATION> builder = new ParserBuilder<>();

        builder.newRule("PROGRAM")
                .concat(TYPE.PROGRAM).many().rule("UNIT").match(";").manyEnd().end();

        builder.newRule("UNIT")
                .concat(TYPE.LABELED).rule("LINE").match(":").rule("LABEL")
                .or()
                .rule("LINE")
                .end();

        builder.newRule("LINE")
                .rule("BRANCH").or().rule("GOTO").or().rule("STACK").or().rule("ASSIGN")
                .end();

        builder.newRule("BRANCH")
                .concat(TYPE.BRANCH).match("if").rule("CONDITION").match("then").rule("GOTO")
                .end();

        builder.newRule("CONDITION")
                .concat(TYPE.CONDITION).rule("OPERAND").rule("REL_OP").rule("OPERAND")
                .end();

        builder.newRule("GOTO")
                .concat(TYPE.GOTO).match("goto").rule("CONSTANT").end();

        builder.newRule("STACK")
                .concat(TYPE.CALL).match("call").rule("CONSTANT")
                .or()
                .keyword(TYPE.RETURN, "return")
                .or()
                .concat(TYPE.PUSH).match("push").rule("OPERAND")
                .or()
                .concat(TYPE.POP).match("pop").rule("ASSIGNABLE")
                .or()
                .concat(TYPE.STACK_OP).match("stack").rule("OPERATOR")
                .end();

        builder.newRule("ASSIGN")
                .concat(TYPE.ASSIGN).rule("ASSIGNABLE").match(":=").rule("EXPR").end();

        builder.newRule("EXPR")
                .concat(TYPE.EXPR).rule("OPERAND").rule("OPERATOR").rule("OPERAND")
                .or()
                .rule("OPERAND")
                .end();

        builder.newRule("OPERAND")
                .rule("ASSIGNABLE").or().rule("CONSTANT").end();

        builder.newRule("ASSIGNABLE")
                .rule("ACCUMULATOR").or().rule("ADDRESS").end();

        builder.newRule("CONSTANT")
                .rule("NUMBER").or().rule("LABEL").end();

        builder.newRule("ADDRESS")
                .concat(TYPE.ADDRESS).match("p").match("\\(").rule("OPERAND").match("\\)")
                .end();

        builder.newRule("ACCUMULATOR")
                .match(TYPE.ACCUMULATOR, "a_\\d+").end();

        builder.newRule("LABEL")
                .match(TYPE.LABEL, "[a-zA-Z]\\w*").end();

        builder.newRule("NUMBER")
                .match(TYPE.NUMBER, "-?\\d+").end();

        builder.newRule("OPERATOR")
                .keyword(TYPE.ADD, "\\+").or()
                .keyword(TYPE.SUB, "\\-").or()
                .keyword(TYPE.MUL, "\\*").or()
                .keyword(TYPE.DIV, "/").or()
                .keyword(TYPE.MOD, "%")
                .end();

        builder.newRule("REL_OP")
                .keyword(TYPE.LEQ, "<=").or()
                .keyword(TYPE.GEQ, ">=").or()
                .keyword(TYPE.LE, "<").or()
                .keyword(TYPE.GE, ">").or()
                .keyword(TYPE.EQ, "=")
                .end();

        return builder.build();
    }
}
