package org.parser.base;

public interface DepthParser<TYPE, ANNOTATION> extends Parser<TYPE, ANNOTATION> {
    void addSubparser(Parser<TYPE, ANNOTATION> subparser);
}
