package org.parser.base;

public interface DepthParser<TYPE> extends Parser<TYPE> {
    /**
     * Inserts a new subparser into the parser
     * @param subparser subparser
     */
    void addSubparser(Parser<TYPE> subparser);

    /**
     * Checks if the parser has subparsers.
     * @return true if the parser has subparsers, false otherwise.
     */
    boolean isEmpty();
}
