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
     * Flags, die angeben, welche Modi es für die Pattern-Erzeugung gibt.
     */
    public enum Ignore {
        IGNORE_WHITESPACE("\\s"), IGNORE_LINEBREAK("\n"), IGNORE_COMMENTS(".");

        private final String value;

        Ignore(String value) {
            this.value = value;
        }

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

    private WhatToIgnore whatToIgnore;

    public Consumable(CharSequence sequence) {
        this.sequence = sequence != null ? sequence : "";
        this.startIndex = 0;
        this.whatToIgnore = new WhatToIgnore();
    }

    public Consumable(CharSequence sequence, Ignore toIgnore) {
        this(sequence);
        this.whatToIgnore = new WhatToIgnore(toIgnore);
    }

    public Consumable(CharSequence sequence, Ignore... toIgnores) {
        this(sequence);
        this.whatToIgnore = new WhatToIgnore(toIgnores);
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
        private final Pattern toIgnore;

        public WhatToIgnore() {
            toIgnore = null;
        }

        public WhatToIgnore(Ignore flag) {
            toIgnore = Pattern.compile(flag.getValue());
        }

        public WhatToIgnore(Ignore ... flags) {
            StringBuilder toIgnoreBuilder = new StringBuilder().append("(");
            toIgnoreBuilder.append(flags[0].getValue());
            for (int i = 1; i < flags.length; i++) {
                toIgnoreBuilder.append("|").append(flags[i].getValue());
            }
            toIgnoreBuilder.append(")*");

            this.toIgnore = Pattern.compile(toIgnoreBuilder.toString());
        }

        public Pattern toIgnore() {
            return toIgnore;
        }
    }
}
