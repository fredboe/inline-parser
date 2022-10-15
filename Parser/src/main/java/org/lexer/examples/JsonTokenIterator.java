package org.lexer.examples;

import org.lexer.CommonIgnores;
import org.lexer.TokenDef;
import org.lexer.TokenIterator;

public class JsonTokenIterator extends TokenIterator<JsonTokenIterator.TYPE> {
    public enum TYPE {
        ARRAY, OBJECT, PROPERTY,
        NUMBER, STRING, TRUE, FALSE, NULL,
        SQ_BRAC_O, SQ_BRAC_C, CURLY_BRAC_O, CURLY_BRAC_C,
        COMMA, COLON
    }

    private static final TokenDef<TYPE> jsonTokenDef = jsonTokenDef();

    public JsonTokenIterator(CharSequence sequence) {
        super(jsonTokenDef, sequence);
    }

    private static TokenDef<TYPE> jsonTokenDef() {
        var tokenDef = new TokenDef<TYPE>();

        tokenDef.addToken(TYPE.STRING, "\"[^\"]*\"")
                .addToken(TYPE.NUMBER, "(\\-)?\\d+(\\.\\d*)?((e|E)(\\+|\\-)?\\d+)?")
                .addToken(TYPE.COLON, ":")
                .addToken(TYPE.COMMA, ",")
                .addToken(TYPE.NULL, "null")
                .addToken(TYPE.CURLY_BRAC_O, "\\{")
                .addToken(TYPE.CURLY_BRAC_C, "\\}")
                .addToken(TYPE.FALSE, "false")
                .addToken(TYPE.TRUE, "true")
                .addToken(TYPE.SQ_BRAC_O, "\\[")
                .addToken(TYPE.SQ_BRAC_C, "\\]");

        tokenDef.addIgnore(CommonIgnores.IGNORE_WHITESPACE)
                .addIgnore(CommonIgnores.IGNORE_COMMENT);

        return tokenDef;
    }
}
