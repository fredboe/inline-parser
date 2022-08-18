package org.parser;

import java.util.function.Function;
import java.util.regex.Pattern;

public class GenPattern {
    /**
     * Flags, die angeben, welche Modi es für die Pattern-Erzeugung gibt.
     */
    public enum Flag {
        NONE, IGNORE_WHITESPACE
    }

    /**
     * Funktion, die eine gegebene RegEx je nach den Flags zu einer anderen RegEx transformiert.
     */
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

    /**
     *
     * @param regex RegEx
     * @return Gibt ein Pattern, für die transformierte RegEx zurück.
     */
    public Pattern getPattern(String regex) {
        return Pattern.compile(transformRegex.apply(regex));
    }

    /**
     * Identitätsfunktion
     */
    private static final Function<String, String> identity = regex -> regex;
    /**
     * Transformiert die RegEx so, dass Leerzeichen am Anfang und am Ende ignoriert werden.
     */
    private static final Function<String, String> ignore_whitespace = regex -> "\\s*" + regex + "\\s*";
}
