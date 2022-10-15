package org.lexer.examples;

import org.lexer.CommonIgnores;
import org.lexer.TokenDef;
import org.lexer.TokenIterator;

public class BranchTokenIterator extends TokenIterator<BranchTokenIterator.TYPE> {
    public enum TYPE {
        IF, LEQ, GEQ, ADD,
        NUMBER, IDENTIFIER,
        ASSIGN, BLOCK,
        IF_TOKEN, LEQ_TOKEN, GEQ_TOKEN, ADD_TOKEN,
        BRAC_O, BRAC_C, CURLY_BRAC_O, CURLY_BRAC_C, SEMICOLON
    }

    private static final TokenDef<TYPE> branchTokenDef = branchTokenDef();

    public BranchTokenIterator(CharSequence sequence) {
        super(branchTokenDef, sequence);
    }

    private static TokenDef<TYPE> branchTokenDef() {
        TokenDef<TYPE> tokenDef = new TokenDef<>();

        tokenDef.addToken(TYPE.IF_TOKEN, "if")
                .addToken(TYPE.LEQ_TOKEN, "<=")
                .addToken(TYPE.GEQ_TOKEN, ">=")
                .addToken(TYPE.ADD_TOKEN, "\\+")
                .addToken(TYPE.NUMBER, "\\d+")
                .addToken(TYPE.IDENTIFIER, "[a-zA-Z]\\w*")
                .addToken(TYPE.BRAC_O, "\\(")
                .addToken(TYPE.BRAC_C, "\\)")
                .addToken(TYPE.CURLY_BRAC_O, "\\{")
                .addToken(TYPE.CURLY_BRAC_C, "\\}")
                .addToken(TYPE.SEMICOLON, ";");

        tokenDef.addIgnore(CommonIgnores.IGNORE_WHITESPACE)
                .addIgnore(CommonIgnores.IGNORE_COMMENT);

        return tokenDef;
    }
}
