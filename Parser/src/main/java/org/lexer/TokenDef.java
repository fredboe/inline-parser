package org.lexer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenDef<Token> {
    /**
     * Tokens with their patterns
     */
    private final List<Pair<Token, Pattern>> tokens;
    /**
     * Patterns to ignore
     */
    private final Set<Pattern> toIgnore;

    public TokenDef() {
        this(new ArrayList<>(), new HashSet<>());
    }

    public TokenDef(List<Pair<Token, Pattern>> tokens, Set<Pattern> ignore) {
        this.tokens = tokens;
        this.toIgnore = ignore;
    }

    public TokenIterator<Token> tokenIterator(CharSequence sequence) {
        return new TokenIterator<>(this, sequence);
    }

    /**
     * Adds a new token to the language.
     * @param token token
     * @param pattern pattern that matches the token
     * @return this
     */
    public TokenDef<Token> addToken(Token token, Pattern pattern) {
        this.tokens.add(new Pair<>(token, pattern));
        return this;
    }

    /**
     * Adds a new token to the language.
     * @param token token
     * @param regex regex that matches the token
     * @return this
     */
    public TokenDef<Token> addToken(Token token, String regex) {
        return addToken(token, Pattern.compile(regex));
    }

    /**
     * Adds a new ignore pattern to the language.
     * @param pattern pattern to ignore
     * @return this
     */
    public TokenDef<Token> addIgnore(Pattern pattern) {
        this.toIgnore.add(pattern);
        return this;
    }

    /**
     * Adds a new ignore pattern to the language.
     * @param regex regex to ignore
     * @return this
     */
    public TokenDef<Token> addIgnore(String regex) {
        return addIgnore(Pattern.compile(regex));
    }

    /**
     *
     * @param sequence sequence
     * @return Returns the next token of the sequence
     */
    public Match<Token> nextToken(CharSequence sequence) {
        var ignoreResult = checkIgnore(sequence);
        if (ignoreResult != null) return ignoreResult;
        return checkToken(sequence);
    }

    private Match<Token> checkIgnore(CharSequence sequence) {
        for (Pattern ignore : toIgnore) {
            var posResult = tryToMatch(sequence, null, ignore);
            if (posResult != null) return posResult;
        }
        return null;
    }

    private Match<Token> checkToken(CharSequence sequence) {
        for (Pair<Token, Pattern> pair : tokens) {
            var posResult = tryToMatch(sequence, pair.x(), pair.y());
            if (posResult != null) return posResult;
        }
        return null;
    }

    private Match<Token> tryToMatch(CharSequence sequence, Token token, Pattern toCheck) {
        Matcher matcher = toCheck.matcher(sequence);
        if (matcher.lookingAt())
            return new Match<>(token, matcher.toMatchResult());
        return null;
    }
}
