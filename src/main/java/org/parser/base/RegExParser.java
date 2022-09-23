package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Regular-Expression Parser
 */
public class RegExParser<TYPE> implements Parser<TYPE> {
    /**
     * RegEx-Pattern
     */
    private final Pattern pattern;
    /**
     * This function is called when the RegEx pattern has been successfully matched.
     * This method should then eventually return the resulting AST.
     */
    private final Function<Consumable.Match, AST<TYPE>> atSuccess;

    public RegExParser(Pattern pattern, Function<Consumable.Match, AST<TYPE>> atSuccess) {
        this.pattern = pattern != null ? pattern : Pattern.compile("");
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicHideAtSuccess();
    }

    /**
     * Checks if the regular expression is successfully matched. If so, atSuccess is called on the returned
     * Match object atSuccess is called, otherwise Optional.empty() is returned.
     * @param consumable Consumable
     * @return An AST wrapped with Optional (empty if the regex is not matched).
     */
    @Override
    public Optional<AST<TYPE>> applyTo(Consumable consumable) {
        Optional<Consumable.Match> match = consumable.lookingAt(pattern);
        return match.map(atSuccess);
    }
}
