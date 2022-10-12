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

    int size();

    Parser<TYPE> get(int i);

    void set(int i, Parser<TYPE> parser);

    default Parser<TYPE> simplify() {
        if (size() == 1) return get(0).simplify();
        for (int i = 0; i < size(); i++) {
            set(i, get(i).simplify());
        }
        return this;
    }
}
