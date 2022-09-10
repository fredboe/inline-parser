package org.parser.examples.alpha;

import org.parser.tree.AST;

import java.util.List;
import java.util.Map;

public class Program {
    private List<AST<Type>> lines; // every ast should have a type of line
    private Map<String, Integer> labels;

    public Program(AST<Type> ast) {
        // do something
    }
}
