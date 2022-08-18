package org.parser;

import java.util.function.Function;
import java.util.regex.Pattern;

public class GenPattern {
    public enum Flag {
        NONE, IGNORE_WHITESPACE
    }

    private final Function<String, String> transformRegex;

    public GenPattern() {
        this.transformRegex = identity;
    }

    public GenPattern(Flag flag) {
        if (flag == Flag.IGNORE_WHITESPACE) {
            this.transformRegex = ignore_whitespace;
        } else {
            this.transformRegex = identity;
        }
    }

    public Pattern getPattern(String regex) {
        return Pattern.compile(transformRegex.apply(regex));
    }


    private static final Function<String, String> identity = regex -> regex;
    private static final Function<String, String> ignore_whitespace = regex -> "\\s*" + regex + "\\s*";
}
