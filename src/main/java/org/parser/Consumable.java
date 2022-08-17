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
     * CharSequence, die konsumierbar sein soll
     */
    private final CharSequence sequence;
    /**
     * Der aktuelle Index, bei der die konsumierte Sequence starten soll (erhöht sich bei Konsumierung)
     */
    private int startIndex;

    public Consumable(CharSequence sequence) {
        this.sequence = sequence != null ? sequence : "";
        this.startIndex = this.sequence.length() > 0 ? 0 : -1;
    }

    public Consumable(Consumable other) {
        this.sequence = other.sequence;
        this.startIndex = other.startIndex;
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
        Matcher matcher = genMatcher(pattern);
        return genMatch(matcher.lookingAt(), matcher);
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
        Matcher matcher = genMatcher(pattern);
        return genMatch(matcher.find(), matcher);
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
        return pattern.matcher(getSequence());
    }

    /**
     *
     * @return Gibt die aktuelle Sequence zurück (mit Konsumierung)
     */
    private CharSequence getSequence() {
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
        return sequence == null || sequence.isEmpty() || startIndex == sequence.length();
    }
}
