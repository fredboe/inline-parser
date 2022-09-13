package org.parser.examples.alpha;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.*;

public class AlphaProgram {
    private List<String> lines;
    private List<AST<Type>> parsedLines;
    private Map<String, Integer> labels;

    public static final ParserPool<Type> alphaPool = AlphaNotationParser.alphaPool();
    private static final Parser<Type> alphaParser = alphaPool.getParser("UNIT");

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
            var label = parsedLine.getChild(1).getMatch();
            labels.put(label.matched(), lineNum);
        }
    }

    private AST<Type> parseLine(String line) throws AlphaError {
        Consumable consLine = consumableOf(line);
        var optionalParsedLine = alphaParser.applyTo(consLine);
        if (optionalParsedLine.isEmpty() || !consLine.isEmpty()) AlphaError.throwParsingError(consLine);

        return optionalParsedLine.map(ast ->
                ast.isType(null) ? new AST<>(Type.PROGRAM) : ast).get(); // empty program (does nothing)
    }
}
