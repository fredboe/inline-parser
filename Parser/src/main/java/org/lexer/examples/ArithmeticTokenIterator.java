package org.lexer.examples;

import org.lexer.CommonIgnores;
import org.lexer.TokenDef;
import org.lexer.TokenIterator;

public class ArithmeticTokenIterator extends TokenIterator<ArithmeticTokenIterator.TYPE> {
    public enum TYPE {
        NUMBER, ADD, SUB, MUL, DIV, POT, FUNC,
        SIN, COS, TAN, PI, E,
        BRAC_O, BRAC_C
    }

    private static final TokenDef<TYPE> arithmeticTokenDef = arithmeticTokenDef();

    public ArithmeticTokenIterator(CharSequence sequence) {
        super(arithmeticTokenDef, sequence);
    }

    private static TokenDef<TYPE> arithmeticTokenDef() {
        TokenDef<TYPE> tokenDef = new TokenDef<>();

        tokenDef.addToken(TYPE.ADD, "\\+")
                .addToken(TYPE.SUB, "\\-")
                .addToken(TYPE.MUL, "\\*")
                .addToken(TYPE.DIV, "/")
                .addToken(TYPE.POT, "\\^")
                .addToken(TYPE.BRAC_O, "\\(")
                .addToken(TYPE.BRAC_C, "\\)")
                .addToken(TYPE.SIN, "sin")
                .addToken(TYPE.COS, "cos")
                .addToken(TYPE.TAN, "tan")
                .addToken(TYPE.E, "e")
                .addToken(TYPE.PI, "pi")
                .addToken(TYPE.NUMBER, "(\\-)?\\d+(\\.\\d*)?((e|E)(\\+|\\-)?\\d+)?");

        tokenDef.addIgnore(CommonIgnores.IGNORE_WHITESPACE)
                .addIgnore(CommonIgnores.IGNORE_COMMENT);

        return tokenDef;
    }
}
