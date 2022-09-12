package org.parser;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CharSequence that can be consumed.
 */
public class Consumable {
    /**
     * Represents a successful RegEx match in the CharSequence.
     * @param matched string that matches the RegEx
     */
    public record Match(String matched) {}

    /**
     * Flags that specify which strings to ignore.
     */
    public enum Ignore {
        IGNORE_H_SPACE("\\h"),
        IGNORE_V_SPACE("\\v"),
        IGNORE_WHITESPACE("\\s"),
        IGNORE_LINEBREAK("\\R"),
        IGNORE_COMMENT("//.*"); // multiline: ((?s)/\*.*\*/)

        private final String value;

        Ignore(String value) {
            this.value = value;
        }

        /**
         *
         * @return Returns the Regular Expression to the Ignore.
         */
        private String getValue() {
            return value;
        }
    }

    /**
     * CharSequence to be consumable
     */
    private final CharSequence sequence;
    /**
     * The current index at which the consumed sequence should start (increases when consumed).
     */
    private int startIndex;

    /**
     * Stores a pattern that contains all regexes that should be ignored.
     */
    private WhatToIgnore whatToIgnore;

    /**
     * Creates a Consumable object with the passed CharSequence, where no strings are ignored.
     * @param sequence CharSequence
     */
    public Consumable(CharSequence sequence) {
        this.sequence = sequence != null ? sequence : "";
        this.startIndex = 0;
        this.whatToIgnore = new WhatToIgnore();
        this.whatToIgnore.build();
    }

    /**
     * Creates a Consumable object with the passed CharSequence and the toIgnores as strings to be ignored.
     * @param sequence CharSequence
     * @param toIgnores strings to be ignored
     */
    public Consumable(CharSequence sequence, Ignore ... toIgnores) {
        this(sequence);
        this.whatToIgnore = new WhatToIgnore(toIgnores);
        this.whatToIgnore.build();
    }

    /**
     * Creates a Consumable object with the passed CharSequence, the toIgnores as strings to be ignored and
     * the comment string as a regular expression for a comment that should also be ignored
     * @param sequence CharSequence
     * @param commentRegEx Regular Expression, which represents a Comment
     * @param toIgnores strings to be ignored
     */
    public Consumable(CharSequence sequence, String commentRegEx, Ignore ... toIgnores) {
        this(sequence);
        this.whatToIgnore = new WhatToIgnore(toIgnores);
        this.whatToIgnore.addIgnore(commentRegEx);
        this.whatToIgnore.build();
    }

    public Consumable(Consumable other) {
        this.sequence = other.sequence;
        this.startIndex = other.startIndex;
        this.whatToIgnore = other.whatToIgnore;
    }

    /**
     * Calls lookingAt with the pattern and if the regex is found, the sequence is consumed until the
     * end of the match found is consumed.
     * @param pattern RegEx pattern
     * @return Returns the match object of the string matching the RegEx, if the RegEx failed,
     * optional.empty() is returned
     */
    public Optional<Match> lookingAt(Pattern pattern) {
        if (isEmpty()) return Optional.empty();

        ignore();
        Matcher matcher = genMatcher(pattern);
        Optional<Match> res = genMatch(matcher.lookingAt(), matcher);
        ignore();
        return res;
    }

    /**
     * Calls lookingAt with the pattern and if the regex is found, the sequence is consumed until the
     * end of the match found is consumed.
     * @param regex RegEx string
     * @return Returns the match object of the string matching the regex if the regex failed,
     * optional.empty() is returned
     */
    public Optional<Match> lookingAt(String regex) {
        return lookingAt(Pattern.compile(regex));
    }

    /**
     * Ruft find mit dem Pattern auf und falls die RegEx gefunden wurde, wird die Sequence bis zum
     * Ende des gefundenen Matches konsumiert.
     * @param pattern RegEx-Pattern
     * @return Gibt das Match-Objekt der zur RegEx passenden Zeichenkette zurück, falls die RegEx fehlschlug,
     *         wird Optional.empty() zurückgegeben
     */
    public Optional<Match> find(Pattern pattern) {
        if (isEmpty()) return Optional.empty();

        ignore();
        Matcher matcher = genMatcher(pattern);
        Optional<Match> res = genMatch(matcher.find(), matcher);
        ignore();
        return res;
    }

    /**
     * Calls find with the pattern and if the regex is found, the sequence is consumed to the
     * end of the match found is consumed.
     * @param regex RegEx string
     * @return Returns the match object of the string matching the regex if the regex failed,
     * optional.empty() is returned
     */
    public Optional<Match> find(String regex) {
        return find(Pattern.compile(regex));
    }

    /**
     * Attempts to match the toIgnore pattern and ignores the result.
     */
    private void ignore() {
        Pattern pattern = whatToIgnore.toIgnore();
        if (pattern != null) {
            Matcher matcher = genMatcher(pattern);
            genMatch(matcher.lookingAt(), matcher);
        }
    }

    /**
     * Generates a match object based on the passed matcher.
     * @param success success bit
     * @param matcher Matcher
     * @return Returns an Optional<Match> object.
     */
    private Optional<Match> genMatch(boolean success, Matcher matcher) {
        if (success) {
            startIndex += matcher.end() - matcher.start();
            return Optional.of(new Match(matcher.group()));
        }
        return Optional.empty();
    }

    /**
     * Generates a Matcher object based on the passed pattern and the current sequebce.
     * @param pattern Pattern
     * @return Returns a Matcher object
     */
    private Matcher genMatcher(Pattern pattern) {
        return pattern.matcher(getSequenceLeft());
    }

    /**
     *
     * @return Returns the current sequence (with consumption)
     */
    public CharSequence getSequenceLeft() {
        if (isEmpty()) return "";
        return sequence.subSequence(startIndex, sequence.length());
    }

    /**
     * Resets the current consumable object to the passed consumable object if
     * the Sequence objects of both match
     * @param other Consumable object
     */
    public void resetTo(Consumable other) {
        if (other.sequence == this.sequence) this.startIndex = other.startIndex;
    }

    /**
     *
     * @return Returns whether the consumable object still has a (non-consumed) character.
     */
    public boolean isEmpty() {
        return sequence == null || sequence.isEmpty() || startIndex >= sequence.length();
    }

    /**
     *
     * @return Creates a string from the remaining CharSequence.
     */
    public String toString() {
        return getSequenceLeft().toString();
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;

        if (obj instanceof Consumable other) {
            return Objects.equals(this.getSequenceLeft(), other.getSequenceLeft());
        } else if (obj instanceof CharSequence sequence) {
            return Objects.equals(this.getSequenceLeft(), sequence);
        }

        return false;
    }


    /**
     * Stores a pattern to be ignored as prefix
     */
    private static class WhatToIgnore {
        /**
         * Pattern that represents all strings to be ignored.
         */
        private Pattern toIgnore = null;
        /**
         * StringBuilder, which is used to build the pattern.
         */
        private StringBuilder toIgnoreBuilder;

        public WhatToIgnore() {
            toIgnoreBuilder = new StringBuilder().append("(");
        }

        /**
         * Adds the passed ignores to the ignoring strings.
         * @param flags to be ignored strings
         */
        public WhatToIgnore(Ignore ... flags) {
            toIgnoreBuilder = new StringBuilder().append("(");
            for (Ignore flag : flags) {
                toIgnoreBuilder.append(flag.getValue()).append("|");
            }
        }

        /**
         * Adds a regex to the ignoring strings.
         * @param regex Regular Expression
         */
        public void addIgnore(String regex) {
            if (toIgnoreBuilder != null) toIgnoreBuilder.append(regex).append("|");
        }

        /**
         * Adds a flag to the ignoring strings.
         * @param flag to be ignored string
         */
        public void addIgnore(Ignore flag) {
            if (toIgnoreBuilder != null) toIgnoreBuilder.append(flag.getValue()).append("|");
        }

        /**
         * Builds a pattern from the ignoring strings.
         */
        public void build() {
            if (toIgnoreBuilder.length() == 1) {
                toIgnore = Pattern.compile("");
            } else {
                toIgnoreBuilder.setCharAt(toIgnoreBuilder.length() - 1, ')');
                toIgnoreBuilder.append("*");
                toIgnore = Pattern.compile(toIgnoreBuilder.toString());
            }
            toIgnoreBuilder = null;
        }

        /**
         *
         * @return Returns the generated pattern to be ignored (must be called after build()).
         */
        public Pattern toIgnore() {
            return toIgnore;
        }
    }
}
