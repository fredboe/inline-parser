package org.parser.base.build;

import org.parser.base.Parser;

import java.util.Map;

/**
 * Contains multiple parsers that can be accessed with one name (created by ParserBuilder).
 * @param <TYPE> type for the AST
 */
public class ParserPool<TYPE> {
    /**
     * Map with names of parsers as key and the parser as value
     */
    private final Map<String, Parser<TYPE>> parsers;

    public ParserPool(Map<String, Parser<TYPE>> parsers) {
        this.parsers = parsers;
    }


    /**
     *
     * @return Returns all named parsers.
     */
    public Map<String, Parser<TYPE>> getParsers() {
        return parsers;
    }

    /**
     *
     * @param name Parser name
     * @return Returns a parser with the name. If there is no parser with that name, null
     * is returned.
     */
    public Parser<TYPE> getParser(String name) {
        return parsers.get(name);
    }
}
