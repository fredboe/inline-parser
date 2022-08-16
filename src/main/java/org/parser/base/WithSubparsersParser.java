package org.parser.base;

import java.util.List;

public interface WithSubparsersParser<TYPE, ANNOTATION> extends Parser<TYPE, ANNOTATION> {
    /**
     * Setzt die Subparser-Liste auf die übergebene Liste
     * @param parsers Subparser-Liste
     */
    void setSubparsers(List<Parser<TYPE, ANNOTATION>> parsers);
}
