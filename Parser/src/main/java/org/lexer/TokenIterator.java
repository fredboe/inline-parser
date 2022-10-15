package org.lexer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// prototype
public class TokenIterator<Token> implements Iterator<Match<Token>> {
    private final List<Match<Token>> tokenList;
    private int position;

    public TokenIterator(TokenDef<Token> tokenDef, CharSequence sequence) {
        this.tokenList = generateTokenList(tokenDef, sequence);
        this.position = 0;
    }

    public TokenIterator(TokenIterator<Token> other) {
        this.tokenList = other.tokenList;
        this.position = other.position;
    }

    /**
     * Generates a list of tokens from the tokenDef and the sequence
     * @param tokenDef definition of all tokens
     * @param sequence sequence
     * @return Returns a list of tokens
     */
    private List<Match<Token>> generateTokenList(TokenDef<Token> tokenDef, CharSequence sequence) {
        List<Match<Token>> tokenList = new ArrayList<>();

        Match<Token> match;
        while ((match = tokenDef.nextToken(sequence)) != null) {
            if (match.hasToken()) tokenList.add(match);
            sequence = consume(sequence, match);
        }

        return tokenList;
    }

    private CharSequence consume(CharSequence sequence, Match<Token> match) {
        if (match != null)
            return sequence.subSequence(match.matchResult().end(), sequence.length());
        return sequence;
    }

    @Override
    public boolean hasNext() {
        return position < tokenList.size();
    }

    @Override
    public Match<Token> next() {
        var result = tokenList.get(position);
        position++;
        return result;
    }

    public String toString() {
        return tokenList.toString();
    }
}
