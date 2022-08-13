package org.parser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Consumable {
    public record Match(String matched, int start, int end) {}

    private CharSequence sequence;
    private int startIndex;
    public Consumable(CharSequence sequence) {
        this.sequence = sequence;
        this.startIndex = sequence.length() > 0 ? 0 : -1;
    }

    public Consumable(Consumable other) {
        this.sequence = other.sequence;
        this.startIndex = other.startIndex;
    }

    public Optional<Match> lookingAt(Pattern pattern) {
        if (isEmpty()) return Optional.empty();
        Matcher matcher = pattern.matcher(sequence.subSequence(startIndex, sequence.length()));
        return genMatch(matcher.lookingAt(), matcher);
    }

    public Optional<Match> lookingAt(String regex) {
        return lookingAt(Pattern.compile(regex));
    }

    public Optional<Match> find(Pattern pattern) {
        if (isEmpty()) return Optional.empty();
        Matcher matcher = genMatcher(pattern);
        return genMatch(matcher.find(), matcher);
    }

    public Optional<Match> find(String regex) {
        return find(Pattern.compile(regex));
    }

    private Optional<Match> genMatch(boolean success, Matcher matcher) {
        if (success) {
            startIndex += matcher.end() - matcher.start();
            return Optional.of(new Match(matcher.group(), matcher.start(), matcher.end()));
        }
        return Optional.empty();
    }

    private Matcher genMatcher(Pattern pattern) {
        return pattern.matcher(getSequence());
    }

    private CharSequence getSequence() {
        return sequence.subSequence(startIndex, sequence.length());
    }

    public void resetTo(Consumable other) {
        this.sequence = other.sequence;
        this.startIndex = other.startIndex;

    }
    public boolean isEmpty() {
        return sequence == null || sequence.isEmpty();
    }
}
