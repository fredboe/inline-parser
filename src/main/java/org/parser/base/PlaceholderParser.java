package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Objects;
import java.util.Optional;

public class PlaceholderParser<TYPE> implements SingleParser<TYPE> {
    private final String ruleName;
    private Parser<TYPE> subparser;

    public PlaceholderParser(String ruleName) {
        this.subparser = null;
        this.ruleName = ruleName;
    }

    @Override
    public Optional<AST<TYPE>> behave(Consumable consumable, Session<TYPE> session) {
        return Optional.ofNullable(subparser).flatMap(parser -> parser.applyTo(consumable, session));
    }

    public String getName() {
        return ruleName;
    }

    public void setParserIfNull(Parser<TYPE> parser) {
        if (this.subparser == null) this.subparser = parser;
    }

    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;

        if (other instanceof PlaceholderParser<?> parser) {
            return  this.subparser.equals(parser.subparser);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(ruleName, subparser);
    }
}
