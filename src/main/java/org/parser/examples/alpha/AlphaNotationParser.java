package org.parser.examples.alpha;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.Mode;
import org.parser.base.build.ParserBuilder;
import org.parser.base.build.ParserPool;
import org.parser.base.build.Simplerule;
import org.parser.tree.AST;

import java.util.Optional;

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

    @Override
    public Consumable consumableOf(CharSequence sequence) {
        return new Consumable(sequence, Consumable.Ignore.IGNORE_H_SPACE, Consumable.Ignore.IGNORE_COMMENT);
    }

    /**
     * Grammar: <br>
     * PROGRAM ::= (UNIT ENDL)* <br>
     * UNIT ::= LINE ":" LABEL | LINE <br>
     * LINE ::= BRANCH | GOTO | ASSIGN | FUNC | STACK | OUTPUT <br>
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
     * OUTPUT ::= "mem" | "clear" | EXE | PRINT <br>
     * EXE ::= "exe" filename <br>
     * PRINT ::= VALUE
     * ENDL ::= "\R" <br>
     * @return Returns a ParserPool for the alpha notation.
     */
    public static ParserPool<Type> alphaPool() {
        ParserBuilder<Type> builder = new ParserBuilder<>();

        builder.newRule("PROGRAM")
                .many(Type.PROGRAM, new Simplerule<Type>().rule("UNIT").hide("\\R"))
                .end();

        builder.newRule("UNIT")
                .type(Type.LABELED).rule("LINE").hide(":").rule("LABEL")
                .or()
                .optional("LINE") // optional for blank lines
                .end();

        builder.newRule("LINE")
                .rule("BRANCH").or().rule("GOTO").or()
                .rule("ASSIGN").or().rule("FUNC").or()
                .rule("OUTPUT").or().rule("STACK").end();

        builder.newRule("BRANCH")
                .type(Type.BRANCH).hide("if").rule("CONDITION").hide("then").rule("GOTO")
                .end();

        builder.newRule("CONDITION")
                .type(Type.CONDITION).rule("VALUE").rule("COMP_OPERATOR").rule("VALUE")
                .end();

        builder.newRule("GOTO")
                .type(Type.END).hide("goto").hide("(?!end\\w)end") // "end" without another letter after it
                .or()
                .type(Type.GOTO).hide("goto").rule("VALUE")
                .end();

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
                .keyword(Type.ADD, "\\+").or().keyword(Type.SUB, "\\-").or()
                .keyword(Type.MUL, "\\*").or().keyword(Type.DIV, "/").or()
                .keyword(Type.MOD, "%")
                .end();

        builder.newRule("COMP_OPERATOR")
                .keyword(Type.LEQ, "<=").or().keyword(Type.GEQ, ">=").or()
                .keyword(Type.LE, "<").or().keyword(Type.GE, ">").or()
                .keyword(Type.EQ, "=")
                .end();

        builder.newRule("OUTPUT")
                .keyword(Type.MEM, "mem").or()
                .keyword(Type.CLEAR, "clear").or()
                .rule("EXE").or()
                .rule("LOAD").or()
                .rule("PRINT").end(); // print must be the last since it consumes clear, mem and exe as labels.

        builder.newRule("EXE")
                .type(Mode.justFst()).hide("exe").match(Type.EXE, ".+").end(); // no line terminators

        builder.newRule("LOAD")
                .type(Mode.justFst()).hide("load").match(Type.LOAD, ".+").end(); // no line terminators

        builder.newRule("PRINT")
                .type(Type.PRINT).rule("VALUE").end(); // maybe add .hide("print")

        return builder.build();
    }
}
