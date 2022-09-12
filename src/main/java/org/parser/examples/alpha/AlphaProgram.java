package org.parser.examples.alpha;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.tree.AST;

import java.util.*;

public class AlphaProgram {
    private List<String> lines;
    private List<AST<Type>> parsedLines; // every ast should have a type of line
    private Map<String, Integer> labels;

    private static final Parser<Type> alphaParser = AlphaNotationParser.alphaPool().getParser("UNIT");

    public AlphaProgram() throws AlphaError {
        this(new ArrayList<>());
    }

    public AlphaProgram(List<String> lines) throws AlphaError {
        this.lines = new ArrayList<>(lines.size());
        this.parsedLines = new ArrayList<>(lines.size());
        this.labels = new HashMap<>();

        for (String line : lines) {
            addLine(line);
        }
    }

    public AlphaProgram(AST<Type> ast) {
        parsedLines = new ArrayList<>();
        labels = new HashMap<>();
        if (ast.getType() == Type.PROGRAM) {
            parsedLines = ast.getChildren().stream()
                    .map(child -> child.isType(Type.LABELED) ? child.getChild(0) : child)
                    .toList();

            int lineNum = 0;
            for (var line : ast.getChildren()) {
                if (line.isType(Type.LABELED)) {
                    var optionalMatch = line.getChild(1).getMatch();
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

    public String getLine(int lineNum) {
        if (lineNum >= lines.size() || lineNum < 0) return "";
        return lines.get(lineNum);
    }

    public AST<Type> getParsedLine(int lineNum) {
        return parsedLines.get(lineNum);
    }

    public int size() {
        return parsedLines.size();
    }

    public void addLine(String line) throws AlphaError {
        int lineNum = lines.size();
        var parsedLine = parseLine(line);
        // changes to this object should be made after this line
        processParsedLine(parsedLine, lineNum);
        lines.add(line);
    }

    public void clear() {
        lines = new ArrayList<>();
        parsedLines = new ArrayList<>();
        labels = new HashMap<>();
    }

    private Consumable consumableOf(String line) {
        return new Consumable(line, Consumable.Ignore.IGNORE_H_SPACE, Consumable.Ignore.IGNORE_COMMENT);
    }

    private void processParsedLine(AST<Type> parsedLine, int lineNum) {
        if (!parsedLine.isType(Type.LABELED)) {
            parsedLines.add(parsedLine);
        } else {
            parsedLines.add(parsedLine.getChild(0));
            var optionalLabel = parsedLine.getChild(1).getMatch();
            optionalLabel.ifPresent(match -> labels.put(match.matched(), lineNum));
        }
    }

    private AST<Type> parseLine(String line) throws AlphaError {
        Consumable consLine = consumableOf(line);
        var optionalParsedLine = alphaParser.applyTo(consLine);
        if (optionalParsedLine.isEmpty() || !consLine.isEmpty()) AlphaError.throwParsingError(consLine);
        return optionalParsedLine.orElseGet(() -> new AST<>(Type.PROGRAM)); // empty program (does nothing)
    }
}
