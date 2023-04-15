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
        this.simplerule = new ConcatParser<>(Mode.takeChildrenIfTypeNull(type));
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

    /**
     * Adds a new match-parser to the simplerule.
     * @param type Type
     * @param pattern Pattern to look for
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> match(TYPE type, Pattern pattern) {
        return addSubparser(Parser.match(type, pattern));
    }

    /**
     * Adds a new match-parser to the simplerule.
     * @param type Type
     * @param regex RegEx to look for
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> match(TYPE type, String regex) {
        return match(type, Pattern.compile(regex));
    }

    /**
     * Adds a new hide-parser to the simplerule.
     * @param pattern Pattern to look for
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> hide(Pattern pattern) {
        return addSubparser(Parser.hide(pattern));
    }

    /**
     * Adds a new hide-parser to the simplerule.
     * @param regex RegEx to look for
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> hide(String regex) {
        return hide(Pattern.compile(regex));
    }

    /**
     * Adds a new keyword-parser to the simplerule.
     * @param type Type
     * @param pattern Pattern to look for
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> keyword(TYPE type, Pattern pattern) {
        return addSubparser(Parser.keyword(type, pattern));
    }

    /**
     * Adds a new keyword-parser to the simplerule.
     * @param type Type
     * @param regex RegEx to look for
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> keyword(TYPE type, String regex) {
        return keyword(type, Pattern.compile(regex));
    }

    /**
     * Adds the rule with the given name to the simplerule.
     * @param name rule name
     * @return Returns the underlying simplerule.
     */
    public Simplerule<TYPE> rule(String name) {
        return addSubparser(parserBuilder.getPlaceholder(name));
    }

    /**
     * Freezes this simplerule so that a method invocation does not change this simplerule.
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Adds a parser to the simplerule.
     * @param parser Parser
     * @return Returns the underlying simplerule.
     */
    private Simplerule<TYPE> addSubparser(Parser<TYPE> parser) {
        if (!frozen) simplerule.addSubparser(parser);
        return this;
    }
}
