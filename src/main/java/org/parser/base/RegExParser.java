package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Regular-Expression Parser
 */
public class RegExParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    /**
     * RegEx-Pattern
     */
    private final Pattern pattern;
    /**
     * Diese Funktion wird aufgerufen, wenn das RegEx-Pattern erfolgreich gematcht wurde.
     * Diese Methode soll letztendlich dann den resultierenden AST liefern.
     */
    private final Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess;

    public RegExParser(Pattern pattern, Function<Consumable.Match, AST<TYPE, ANNOTATION>> atSuccess) {
        this.pattern = pattern != null ? pattern : Pattern.compile("");
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicHideAtSuccess();
    }

    /**
     * Prüft, ob die Regular-Expression erfolgreich gematcht wird. Wenn ja, wird auf dem zurückgegebenen
     * Match Objekt atSuccess aufgerufen, ansonsten wird Optional.empty() zurückgegeben.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (empty, falls die RegEx nicht gematcht wird)
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Optional<Consumable.Match> match = consumable.lookingAt(pattern);
        return match.map(atSuccess);
    }
}
