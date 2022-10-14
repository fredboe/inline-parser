package org.lexer;

import java.util.regex.MatchResult;

public record Match<Token>(Token token, MatchResult matchResult) {
    public boolean hasToken() {
        return token != null;
    }
}
