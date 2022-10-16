package org.parser.alpha;

import org.lexer.CommonIgnores;
import org.lexer.TokenDef;
import org.lexer.TokenIterator;

public class AlphaTokenIterator extends TokenIterator<Type> {
    private static final TokenDef<Type> alphaTokenDef = alphaTokenDef();

    public AlphaTokenIterator(CharSequence sequence) {
        super(alphaTokenDef, sequence);
    }

    private static TokenDef<Type> alphaTokenDef() {
        TokenDef<Type> tokenDef = new TokenDef<>();

        tokenDef.addToken(Type.ADD, "\\+")
                .addToken(Type.SUB, "\\-")
                .addToken(Type.MUL, "\\*")
                .addToken(Type.DIV, "/")
                .addToken(Type.MOD, "%")
                .addToken(Type.LEQ, "<=")
                .addToken(Type.LE, "<")
                .addToken(Type.GEQ, ">=")
                .addToken(Type.GE, ">")
                .addToken(Type.EQ, "=")
                .addToken(Type.MEM, "mem")
                .addToken(Type.GOTO, "goto")
                .addToken(Type.CALL, "call")
                .addToken(Type.RETURN, "return")
                .addToken(Type.ASSIGN, ":=")
                .addToken(Type.CLEAR, "clear")
                .addToken(Type.PRINT, "print")
                .addToken(Type.EXE, "exe")
                .addToken(Type.END, "(?!end\\w)end")
                .addToken(Type.ALPHA_TOKEN, "(alpha(_)?|a(_)?)")
                .addToken(Type.RHO_TOKEN, "rho|p")
                .addToken(Type.IF_TOKEN, "if")
                .addToken(Type.THEN_TOKEN, "then")
                .addToken(Type.PUSH, "push")
                .addToken(Type.POP, "pop")
                .addToken(Type.STACK_OP, "stack")
                .addToken(Type.BRAC_O, "\\(")
                .addToken(Type.BRAC_C, "\\)")
                .addToken(Type.COLON, ":")
                .addToken(Type.LINEBREAK, "\\R")
                .addToken(Type.NUMBER, "(\\-)?\\d+")
                .addToken(Type.LABEL, "[a-zA-Z]\\w*");

        tokenDef.addIgnore(CommonIgnores.IGNORE_H_SPACE)
                .addIgnore(CommonIgnores.IGNORE_COMMENT);

        return tokenDef;
    }
}
