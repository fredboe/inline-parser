package org.parser.examples.alpha;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.Mode;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.regex.Pattern;

public class AlphaNotationParser implements Parser<Type> {
    private final Parser<Type> alphaParser;

    public AlphaNotationParser() {
        alphaParser = alphaPool().getParser("PROGRAM");
    }

    @Override
    public Optional<AST<Type>> applyTo(Consumable consumable) {
        return alphaParser.applyTo(consumable);
    }

    @Override
    public Optional<AST<Type>> applyTo(CharSequence sequence) {
        return alphaParser.applyTo(new Consumable(sequence,
                Consumable.Ignore.IGNORE_H_SPACE,
                Consumable.Ignore.IGNORE_COMMENT)
        );
    }

    /**
     * Grammar: <br>
     * PROGRAM ::= UNIT* <br>
     * UNIT ::= LINE ":" LABEL ";" | LINE ";" <br>
     * LINE ::= BRANCH | GOTO | ASSIGN | FUNC | STACK <br>
     * BRANCH ::= "if" "(" CONDITION ")" GOTO <br>
     * CONDITION ::= VALUE COMP_OPERATOR VALUE <br>
     * GOTO ::= "goto" VALUE <br>
     * VALUE ::= NUMBER | ACCUMULATOR | ADDRESS <br>
     * ASSIGNABLE ::= ACCUMULATOR | ADDRESS <br>
     * ASSIGN ::= ASSIGNABLE ":=" EXPR <br>
     * EXPR ::= VALUE OPERATOR VALUE <br>
     * FUNC ::= "call" VALUE | "return" <br>
     * STACK ::= "push" VALUE | "pop" VALUE | "stack_op" OPERATOR <br>
     * ACCUMULATOR ::= a_\d+ <br>
     * ADDRESS ::= "p" "(" VALUE ")" <br>
     * NUMBER ::= (\-)?\d+ <br>
     * LABEL ::= [a-zA-Z]\w* <br>
     * OPERATOR ::= "+" | "-" | "*" | "/" | "%" <br>
     * COMP_OPERATOR ::= "<=" | ">=" | "<" | ">" | "=" <br>
     * ENDL ::= "\n" <br>
     * @return Returns a ParserPool for the alpha notation.
     */
    public static ParserPool<Type> alphaPool() {
        ParserBuilder<Type> builder = new ParserBuilder<>();

        builder.newRule("PROGRAM").many(Type.PROGRAM, "UNIT").end();

        builder.newRule("UNIT")
                .type(Type.LABELED).rule("LINE").hide(":").rule("LABEL").hide("\\R")
                .or()
                .type(Mode.justFst()).optional("LINE").hide("\\R") // optional for blank lines
                .end();

        builder.newRule("LINE")
                .rule("BRANCH").or().rule("GOTO").or()
                .rule("ASSIGN").or().rule("FUNC").or()
                .rule("STACK").end();

        builder.newRule("BRANCH")
                .type(Type.BRANCH).hide("if").rule("CONDITION").hide("then").rule("GOTO")
                .end();

        builder.newRule("CONDITION")
                .type(Type.CONDITION).rule("VALUE").rule("COMP_OPERATOR").rule("VALUE")
                .end();

        builder.newRule("GOTO")
                .type(Type.GOTO).hide("goto").rule("VALUE").end();

        builder.newRule("VALUE")
                .rule("ACCUMULATOR").or().rule("ADDRESS").or().rule("NUMBER").or().rule("LABEL")
                .end();

        builder.newRule("ASSIGNABLE")
                .rule("ACCUMULATOR").or().rule("ADDRESS").end();

        builder.newRule("ASSIGN")
                .type(Type.ASSIGN).rule("ASSIGNABLE").hide(":=").rule("EXPR")
                .end();

        builder.newRule("EXPR")
                .type(Type.EXPR).rule("VALUE").rule("OPERATOR").rule("VALUE")
                .or()
                .rule("VALUE")
                .end();

        builder.newRule("FUNC")
                .type(Type.CALL).hide("call").rule("VALUE")
                .or()
                .keyword(Type.RETURN, "return")
                .end();

        builder.newRule("STACK")
                .type(Type.PUSH).hide("push").rule("VALUE")
                .or()
                .type(Type.POP).hide("pop").rule("ASSIGNABLE")
                .or()
                .type(Type.STACK_OP).hide("stack_op").rule("OPERATOR")
                .end();

        builder.newRule("ACCUMULATOR")
                .type(Mode.justFst()).hide("a_").match(Type.ACCUMULATOR,"\\d+").end();

        builder.newRule("ADDRESS")
                .type(Type.ADDRESS).hide("p").hide("\\(").rule("VALUE").hide("\\)")
                .end();

        builder.newRule("NUMBER")
                .match(Type.NUMBER, "(\\-)?\\d+").end();

        builder.newRule("LABEL")
                .match(Type.LABEL, "[a-zA-Z]\\w*").end();

        builder.newRule("OPERATOR")
                .match(Type.ADD, "\\+").or().match(Type.SUB, "\\-").or()
                .match(Type.MUL, "\\*").or().match(Type.DIV, "/").or()
                .match(Type.MOD, "%")
                .end();

        builder.newRule("COMP_OPERATOR")
                .match(Type.LEQ, "<=").or().match(Type.GEQ, ">=").or()
                .match(Type.LE, "<").or().match(Type.GE, ">").or()
                .match(Type.EQ, "=")
                .end();

        return builder.build();
    }
}
