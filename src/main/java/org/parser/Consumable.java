package org.parser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CharSequence, die konsumiert werden kann
 */
public class Consumable {
    /**
     * Repräsentiert einen erfolgreichen RegEx-Match in der CharSequence
     * @param matched Zeichenkette, die mit der RegEx übereinstimmt
     * @param start Start Index (matcher.start())
     * @param end End Index (matcher.end())
     */
    public record Match(String matched, int start, int end) {}

    /**
     * Flags, die angeben, welche Zeichenketten ignoriert werden sollen.
     */
    public enum Ignore {
        IGNORE_WHITESPACE("\\s"), IGNORE_LINEBREAK("\n"), IGNORE_COMMENT("//.*\n|((?s)/\\*.*\\*/)");

        private final String value;

        Ignore(String value) {
            this.value = value;
        }

        /**
         *
         * @return Gibt die Regular Expression zu dem Ignore zurück.
         */
        private String getValue() {
            return value;
        }
    }

    /**
     * CharSequence, die konsumierbar sein soll
     */
    private final CharSequence sequence;
    /**
     * Der aktuelle Index, bei der die konsumierte Sequence starten soll (erhöht sich bei Konsumierung)
     */
    private int startIndex;

    /**
     * Speichert ein Pattern, dass alle RegEx enthält, die ignoriert werden sollen
     */
    private WhatToIgnore whatToIgnore;

    /**
     * Erzeugt ein Consumable-Objekt mit der übergebenen CharSequence, bei der keine Zeichenketten ignoriert werden.
     * @param sequence CharSequence
     */
    public Consumable(CharSequence sequence) {
        this.sequence = sequence != null ? sequence : "";
        this.startIndex = 0;
        this.whatToIgnore = new WhatToIgnore();
        this.whatToIgnore.build();
    }

    /**
     * Erzeugt ein Consumable-Objekt mit der übergebenen CharSequence und den toIgnores als zu ignorierenden Zeichenketten.
     * @param sequence CharSequence
     * @param toIgnores zu ignorierende Zeichenketten
     */
    public Consumable(CharSequence sequence, Ignore ... toIgnores) {
        this(sequence);
        this.whatToIgnore = new WhatToIgnore(toIgnores);
        this.whatToIgnore.build();
    }

    /**
     * Erzeugt ein Consumable-Objekt mit der übergebenen CharSequence, den toIgnores als zu ignorierenden Zeichenketten und
     * dem comment-String als Regular Expression für einen Comment, der auch ignoriert werden soll
     * @param sequence CharSequence
     * @param commentRegEx Regular Expression, die einen Comment repräsentiert
     * @param toIgnores zu ignorierende Zeichenketten
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
     * Ruft lookingAt mit dem Pattern auf und falls die RegEx gefunden wurde, wird die Sequence bis zum
     * Ende des gefundenen Matches konsumiert.
     * @param pattern RegEx-Pattern
     * @return Gibt das Match-Objekt der zur RegEx passenden Zeichenkette zurück, falls die RegEx fehlschlug,
     *         wird Optional.empty() zurückgegeben
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
     * Ruft lookingAt mit dem Pattern auf und falls die RegEx gefunden wurde, wird die Sequence bis zum
     * Ende des gefundenen Matches konsumiert.
     * @param regex RegEx-String
     * @return Gibt das Match-Objekt der zur RegEx passenden Zeichenkette zurück, falls die RegEx fehlschlug,
     *         wird Optional.empty() zurückgegeben
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
     * Ruft find mit dem Pattern auf und falls die RegEx gefunden wurde, wird die Sequence bis zum
     * Ende des gefundenen Matches konsumiert.
     * @param regex RegEx-String
     * @return Gibt das Match-Objekt der zur RegEx passenden Zeichenkette zurück, falls die RegEx fehlschlug,
     *         wird Optional.empty() zurückgegeben
     */
    public Optional<Match> find(String regex) {
        return find(Pattern.compile(regex));
    }

    /**
     * Versucht das toIgnore-Pattern zu matchen und ignoriert das Ergebnis
     */
    private void ignore() {
        Pattern pattern = whatToIgnore.toIgnore();
        if (pattern != null) {
            Matcher matcher = genMatcher(pattern);
            genMatch(matcher.lookingAt(), matcher);
        }
    }

    /**
     * Generiert ein Match-Objekt, basierend auf dem übergebenen Matcher.
     * @param success success-Bit
     * @param matcher Matcher
     * @return Gibt ein Optional<Match> Objekt zurück
     */
    private Optional<Match> genMatch(boolean success, Matcher matcher) {
        if (success) {
            startIndex += matcher.end() - matcher.start();
            return Optional.of(new Match(matcher.group(), matcher.start(), matcher.end()));
        }
        return Optional.empty();
    }

    /**
     * Generiert ein Matcher Objekt basierend auf dem übergebenen Pattern und der aktuellen Sequebce
     * @param pattern Pattern
     * @return Gibt ein Matcher Objekt zurück
     */
    private Matcher genMatcher(Pattern pattern) {
        return pattern.matcher(getSequenceLeft());
    }

    /**
     *
     * @return Gibt die aktuelle Sequence zurück (mit Konsumierung)
     */
    public CharSequence getSequenceLeft() {
        if (isEmpty()) return "";
        return sequence.subSequence(startIndex, sequence.length());
    }

    /**
     * Setzt das aktuelle Consumable Objekt auf das übergebene Consumable-Objekt zurück, falls
     * die Sequence Objekte von beiden übereinstimmen
     * @param other Consumable-Objekt
     */
    public void resetTo(Consumable other) {
        if (other.sequence == this.sequence) this.startIndex = other.startIndex;
    }

    /**
     *
     * @return Gibt zurück, ob das Consumable Objekt noch einen (nicht konsumierten) Character besitzt
     */
    public boolean isEmpty() {
        return sequence == null || sequence.isEmpty() || startIndex >= sequence.length();
    }

    /**
     *
     * @return Erzeugt aus der übriggebliebenen CharSequence einen String.
     */
    public String toString() {
        return getSequenceLeft().toString();
    }


    /**
     * Speichert ein Pattern, welches als prefix ignoriert werden soll
     */
    private static class WhatToIgnore {
        /**
         * Pattern, dass alle zu ignorierenden Zeichenketten repräsentiert.
         */
        private Pattern toIgnore = null;
        /**
         * StringBuilder, mit dem das Pattern gebaut wird
         */
        private StringBuilder toIgnoreBuilder;

        public WhatToIgnore() {
            toIgnoreBuilder = new StringBuilder().append("(");
        }

        /**
         * Fügt die übergebenen Ignores zu den ignorierenden Zeichenketten hinzu
         * @param flags zu ignorierende Zeichenketten
         */
        public WhatToIgnore(Ignore ... flags) {
            toIgnoreBuilder = new StringBuilder().append("(");
            for (Ignore flag : flags) {
                toIgnoreBuilder.append(flag.getValue()).append("|");
            }
        }

        /**
         * Fügt eine RegEx zu den ignorierenden Zeichenketten hinzu
         * @param regex Regular Expression
         */
        public void addIgnore(String regex) {
            if (toIgnoreBuilder != null) toIgnoreBuilder.append(regex).append("|");
        }

        /**
         * Fügt ein Ignore zu den ignorierenden Zeichenketten hinzu
         * @param flag zu ignorierende Zeichenkette
         */
        public void addIgnore(Ignore flag) {
            if (toIgnoreBuilder != null) toIgnoreBuilder.append(flag.getValue()).append("|");
        }

        /**
         * Baut aus den ignorierenden Zeichenketten ein Pattern.
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
         * @return Gibt das erzeugte Pattern zurück, welches ignoriert werden soll (muss nach build() aufgerufen werden).
         */
        public Pattern toIgnore() {
            return toIgnore;
        }
    }
}
