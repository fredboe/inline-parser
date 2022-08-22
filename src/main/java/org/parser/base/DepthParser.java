package org.parser.base;

public interface DepthParser<TYPE, ANNOTATION> extends Parser<TYPE, ANNOTATION> {
    /**
     * Inserts a new subparser into the parser
     * @param subparser subparser
     */
    void addSubparser(Parser<TYPE, ANNOTATION> subparser);

    /**
     * Checks if the parser has subparsers.
     * @return true if the parser has subparsers, false otherwise.
     */
    boolean isEmpty();
}
