package org.parser.base;

import org.parser.Consumable;

import java.util.Optional;

public class PlaceholderParser<TYPE> implements Parser<TYPE> {
    private String name;
    private Parser<TYPE> parser;

    public PlaceholderParser() {
        this.name = null;
        this.parser = null;
    }

    @Override
    public void processWith(Environment<TYPE> environment) {
        Consumable.Mark startMark = environment.createConsumableMark();
        if (environment.hasInCache(startMark, name)) {
            environment.resultStack().push(environment.getFromCache(startMark, name));
        } else {
            environment.executeAndThenCall(parser, (v) -> handleAfterExecution(environment, startMark));
        }
    }

    private void handleAfterExecution(Environment<TYPE> environment, Consumable.Mark startMark) {
        if (parser == null) {
            environment.resultStack().push(Optional.empty());
        } else {
            environment.putToCache(startMark, name, environment.resultStack().peek());
        }
    }

    public void setParserIfNull(String name, Parser<TYPE> parser) {
        if (this.parser == null) {
            this.name = name;
            this.parser = parser;
        }
    }
}
