package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.Parser;

import java.util.regex.Pattern;

public class Simplerule<TYPE> {
    private final ParserBuilder<TYPE> parserBuilder;
    private final ConcatParser<TYPE> simplerule;
    private boolean frozen;

    public Simplerule(TYPE type) {
        this.parserBuilder = new ParserBuilder<>();
        this.simplerule = new ConcatParser<>(Mode.childrenIfNoType(type));
        this.frozen = false;
    }

    public Simplerule() {
        this(null);
    }

    public ParserBuilder<TYPE> parserBuilder() {
        return parserBuilder;
    }

    public ConcatParser<TYPE> parser() {
        return simplerule;
    }

    public Simplerule<TYPE> match(TYPE type, Pattern pattern) {
        return addSubparser(Parser.match(type, pattern));
    }

    public Simplerule<TYPE> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }

    public Simplerule<TYPE> hide(Pattern pattern) {
        return addSubparser(Parser.hide(pattern));
    }

    public Simplerule<TYPE> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    public Simplerule<TYPE> keyword(TYPE type, Pattern pattern) {
        return addSubparser(Parser.keyword(type, pattern));
    }

    public Simplerule<TYPE> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    public Simplerule<TYPE> rule(String name) {
        return addSubparser(parserBuilder.getPlaceholder(name));
    }

    public void freeze() {
        frozen = true;
    }

    private Simplerule<TYPE> addSubparser(Parser<TYPE> parser) {
        if (!frozen) simplerule.addSubparser(parser);
        return this;
    }
}
