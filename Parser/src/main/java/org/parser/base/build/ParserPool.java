package org.parser.base.build;

import org.parser.base.Parser;

import java.util.Map;

/**
 * Contains multiple parsers that can be accessed with one name (created by ParserBuilder).
 * @param <TYPE>  type for the AST
 * @param parsers Map with names of parsers as key and the parser as value
 */
public record ParserPool<TYPE>(Map<String, Parser<TYPE>> parsers) {
    /**
     * @param name Parser name
     * @return Returns a parser with the name. If there is no parser with that name, null
     * is returned.
     */
    public Parser<TYPE> getParser(String name) {
        return parsers.get(name);
    }
}
