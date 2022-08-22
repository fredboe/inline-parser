package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.ManyParser;
import org.parser.base.Parser;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SomeBuilder<TYPE, ANNOTATION> {
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder;

    private ConcatParser<TYPE, ANNOTATION> concatParser;

    private boolean frozen;

    public SomeBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder) {
        this.parserBuilder = parserBuilder;
        this.concatBuilder = concatBuilder;
        this.concatParser = Parser.concat(null, new ArrayList<>());
        this.frozen = true;
    }

    void newSomeRule() {
        if (frozen) {
            concatParser = Parser.concat(null, new ArrayList<>());
            frozen = false;
        }
    }

    public SomeBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    public SomeBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    public SomeBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> someEnd() {
        frozen = true;
        concatBuilder.addStep(concatParser);
        concatBuilder.addStep(new ManyParser<>(null, concatParser));
        return concatBuilder;
    }

    private SomeBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (parser != null && !frozen) concatParser.addSubparser(parser);
        return this;
    }
}
