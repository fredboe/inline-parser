package org.parser.base.build;

import org.parser.base.Parser;

import java.util.Map;

/**
 * Enthält mehrere Parser, auf die mit einem Namen zugegriffen werden kann (entsteht durch ParserBuilder)
 * @param <TYPE> Typ für den AST
 * @param <ANNOTATION> Annotation für den AST
 */
public class ParserPool<TYPE, ANNOTATION> {
    /**
     * Map mit Namen der Parser als Key und dem Parser als Value
     */
    private final Map<String, Parser<TYPE, ANNOTATION>> parsers;

    public ParserPool(Map<String, Parser<TYPE, ANNOTATION>> parsers) {
        this.parsers = parsers;
    }

    /**
     *
     * @param name Parser-Name
     * @return Gibt einen Parser mit dem Namen zurück. Falls es keinen Parser mit diesem Namen gibt, wird null
     *         zurückgegeben.
     */
    public Parser<TYPE, ANNOTATION> getParser(String name) {
        return parsers.get(name);
    }
}
