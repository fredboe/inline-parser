package org.lexer;

import java.util.regex.MatchResult;

public record Match<Token>(Token token, MatchResult matchResult) {
    public boolean hasToken() {
        return token != null;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Match<?> other) {
            return this.token.equals(other.token);
        }
        return false;
    }
}
