package org.parser.base;

public interface DepthParser<TYPE, ANNOTATION> extends Parser<TYPE, ANNOTATION> {
    /**
     * Fügt dem Parser einen neuen Subparser ein
     * @param subparser Subparser
     */
    void addSubparser(Parser<TYPE, ANNOTATION> subparser);

    /**
     * Prüft, ob der Parser Subparser besitzt.
     * @return true, falls der Parser Subparser besitzt, sonst false
     */
    boolean isEmpty();
}
