package org.parser.base;

import org.parser.Consumable;
import org.parser.Parser;
import org.parser.tree.AST;

import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

public class RegExParser<TYPE, ANNOTATIONS> implements Parser<TYPE, ANNOTATIONS> {
    private final Pattern pattern;

    private final Function<Consumable.Match, AST<TYPE, ANNOTATIONS>> atSuccess;

    public RegExParser(Pattern pattern, Function<Consumable.Match, AST<TYPE, ANNOTATIONS>> atSuccess) {
        this.pattern = pattern;
        this.atSuccess = atSuccess;
    }

    @Override
    public Optional<AST<TYPE, ANNOTATIONS>> applyTo(Consumable consumable) {
        Optional<Consumable.Match> match = consumable.lookingAt(pattern);
        return match.map(atSuccess);
    }


    // name schlecht gewählt
    public static <TYPE, ANNOTATIONS> RegExParser<TYPE, ANNOTATIONS> HideParser(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<TYPE, ANNOTATIONS>(type).ignore());
    }

    public static <TYPE, ANNOTATIONS>RegExParser<TYPE, ANNOTATIONS> KeywordParser(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, null));
    }

    public static <TYPE, ANNOTATIONS>RegExParser<TYPE, ANNOTATIONS> MatchParser(TYPE type, Pattern pattern) {
        return new RegExParser<>(pattern, match -> new AST<>(type, match));
    }
}
