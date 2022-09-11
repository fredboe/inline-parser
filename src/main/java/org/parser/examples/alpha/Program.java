package org.parser.examples.alpha;

import org.parser.tree.AST;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Program {
    private List<AST<Type>> lines; // every ast should have a type of line
    private Map<String, Integer> labels;

    public Program(AST<Type> ast) {
        if (ast.getType() == Type.PROGRAM) {
            lines = ast.getChildren().stream()
                    .map(child -> child.isType(Type.LABELED) ? child.getChild(0) : child)
                    .toList();

            int lineNum = 0;
            for (var line : ast.getChildren()) {
                if (line.isType(Type.LABELED)) {
                    var optionalMatch = line.getMatch();
                    int finalLineNum = lineNum;
                    optionalMatch.ifPresent(match -> labels.put(match.matched(), finalLineNum));
                }
                lineNum++;
            }
        }
    }

    public Integer getLineOfLabel(String label) {
        return labels.get(label);
    }

    public AST<Type> getLine(int lineNum) {
        return lines.get(lineNum);
    }

    public int size() {
        return lines.size();
    }
}
