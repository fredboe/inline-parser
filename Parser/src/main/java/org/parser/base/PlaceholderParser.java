package org.parser.base;

import java.util.Optional;

public class PlaceholderParser<TYPE> implements Parser<TYPE> {
    private Parser<TYPE> parser;

    public PlaceholderParser() {
        this.parser = null;
    }

    @Override
    public void processWith(Environment<TYPE> environment) {
        environment.executeAndThenCall(parser, (v) -> {
            if (parser == null) environment.resultStack().push(Optional.empty());
        });
    }

    public void setParserIfNull(Parser<TYPE> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
