package org.lexer;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

// prototype
public class Lexer<Token> {
    /**
     * Tokens with their patterns
     */
    private final Map<Token, Pattern> tokens;
    /**
     * Patterns to ignore
     */
    private final Set<Pattern> ignore;

    public Lexer(Map<Token, Pattern> tokens, Set<Pattern> ignore) {
        this.tokens = tokens;
        this.ignore = ignore;
    }

    /**
     * Adds a new token to the language.
     * @param token token
     * @param pattern pattern that matches the token
     */
    public void addToken(Token token, Pattern pattern) {
        tokens.put(token, pattern);
    }

    /**
     * Adds a new token to the language.
     * @param token token
     * @param regex regex that matches the token
     */
    public void addToken(Token token, String regex) {
        addToken(token, Pattern.compile(regex));
    }

    /**
     * Adds a new ignore pattern to the language.
     * @param pattern pattern to ignore
     */
    public void addIgnore(Pattern pattern) {
        ignore.add(pattern);
    }

    /**
     * Adds a new ignore pattern to the language.
     * @param regex regex to ignore
     */
    public void addIgnore(String regex) {
        addIgnore(Pattern.compile(regex));
    }

    /**
     * Tokenizes the buffer based on the language definition.
     * @param buffer CharBuffer
     * @return token-array
     */
    public List<Match<Token>> tokenize(CharBuffer buffer) {
        List<Match<Token>> tokenArray = new ArrayList<>();

        Pair<CharBuffer, Match<Token>> currentToken;
        while ((currentToken = nextToken(buffer)) != null) {
            var nextBuffer = currentToken.x();
            var match = currentToken.y();

            if (match.hasToken()) tokenArray.add(match);
            buffer = nextBuffer;
        }

        return tokenArray;
    }

    /**
     * Tokenizes the sequence based on the language definition. The sequence is first wrapped to a CharBuffer.
     * @param sequence CharSequence
     * @return token-array
     */
    public List<Match<Token>> tokenize(CharSequence sequence) {
        return tokenize(CharBuffer.wrap(sequence));
    }

    private Pair<CharBuffer, Match<Token>> nextToken(CharBuffer sequence) {
        Pair<CharBuffer, Match<Token>> ignoreMatch = checkIgnores(sequence);
        if (ignoreMatch != null) return ignoreMatch;
        return checkTokens(sequence);
    }

    private Pair<CharBuffer, Match<Token>> checkIgnores(CharBuffer sequence) {
        for (var pattern : ignore) {
            var result = tryToMatch(sequence, null, pattern);
            if (result != null) return result;
        }
        return null;
    }

    private Pair<CharBuffer, Match<Token>> checkTokens(CharBuffer sequence) {
        for (var entry : tokens.entrySet()) {
            var result = tryToMatch(sequence, entry.getKey(), entry.getValue());
            if (result != null) return result;
        }
        return null;
    }

    private Pair<CharBuffer, Match<Token>> tryToMatch(CharBuffer sequence, Token token, Pattern pattern) {
        var matcher = pattern.matcher(sequence);
        if (!matcher.lookingAt()) return null;

        var matchResult = matcher.toMatchResult();
        var nextBuffer = sequence.subSequence(matchResult.end(), sequence.length());
        return new Pair<>(nextBuffer, new Match<>(token, matchResult));
    }
}
