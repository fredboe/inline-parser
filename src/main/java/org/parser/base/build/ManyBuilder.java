package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.ManyParser;
import org.parser.base.Parser;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ManyBuilder<TYPE, ANNOTATION> {
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder;

    private ConcatParser<TYPE, ANNOTATION> concatParser;

    private boolean frozen;

    public ManyBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder,
                       ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder) {
        this.parserBuilder = parserBuilder;
        this.concatBuilder = concatBuilder;
        this.concatParser  = Parser.concat(null, new ArrayList<>());
        this.frozen = true;
    }

    void newManyRule() {
        if (frozen) {
            concatParser = Parser.concat(null, new ArrayList<>());
            frozen = false;
        }
    }

    public ManyBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    public ManyBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    public ManyBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    public ConcatRuleBuilder<TYPE, ANNOTATION> manyEnd() {
        frozen = true;
        concatBuilder.addStep(new ManyParser<>(null, concatParser));
        return concatBuilder;
    }

    private ManyBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (parser != null && !frozen) concatParser.addSubparser(parser);
        return this;
    }
}
