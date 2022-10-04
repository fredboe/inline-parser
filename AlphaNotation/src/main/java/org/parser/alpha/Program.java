package org.parser.alpha;

import org.parser.Consumable;
import org.parser.base.Parser;
import org.parser.base.build.ParserPool;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
    private List<String> lines;
    private List<AST<Type>> parsedLines;
    /**
     * Label-names with their corresponding line number.
     */
    private Map<String, Integer> labels;

    private static final ParserPool<Type> alphaPool = AlphaNotationParser.alphaPool();

    public static final Parser<Type> alphaLineParser = alphaPool.getParser("LINE");

    public Program() throws AlphaError {
        this(new ArrayList<>());
    }

    /**
     * Creates a new program with the given list as lines (each lines is parsed).
     * @param lines List of lines
     * @throws AlphaError at parsing failure.
     */
    public Program(List<String> lines) throws AlphaError {
        this.lines = new ArrayList<>(lines.size());
        this.parsedLines = new ArrayList<>(lines.size());
        this.labels = new HashMap<>();

        for (String line : lines) {
            addLine(line);
        }
    }

    /**
     *
     * @param label Label name
     * @return Returns the line number that corresponds to the given label.
     *         If this label does not exist then null is returned.
     */
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

    /**
     *
     * @return Returns the number of lines in this program.
     */
    public int size() {
        return parsedLines.size();
    }

    public void addLine(String line) throws AlphaError {
        var parsedLine = parseLine(line);
        // changes to this object should be made after this line because an error can be thrown in the first line
        processParsedLine(parsedLine);
        lines.add(line);
    }

    /**
     * Clears this program. Meaning everything is set back.
     */
    public void clear() {
        lines = new ArrayList<>();
        parsedLines = new ArrayList<>();
        labels = new HashMap<>();
    }

    /**
     * Adds the AST to the parsedLines list. If this AST has a type of LABELED then the stored label is
     * added to the label map.
     * @param parsedLine AST
     */
    private void processParsedLine(AST<Type> parsedLine) {
        int lineNum = lines.size();
        if (!parsedLine.isType(Type.LABELED)) {
            parsedLines.add(parsedLine);
        } else {
            parsedLines.add(parsedLine.getChild(0));
            var label = parsedLine.getChild(1).getMatch();
            labels.put(label.matched(), lineNum);
        }
    }

    /**
     *
     * @param line Line
     * @return Returns the fitting AST of the given line.
     * @throws AlphaError at parsing failure.
     */
    private static AST<Type> parseLine(String line) throws AlphaError {
        Consumable consLine = consumableOf(line);
        var optionalParsedLine = alphaLineParser.applyTo(consLine);
        if (optionalParsedLine.isEmpty() || !consLine.isEmpty())
            throw new AlphaError.ParsingException(consLine);

        return optionalParsedLine.map(ast ->
                ast.isType(null) ? new AST<>(Type.NOP) : ast).get(); // empty program (does nothing)
    }

    /**
     *
     * @param program Program/Line-string
     * @return Returns a consumable object for the given string.
     */
    private static Consumable consumableOf(String program) {
        return new Consumable(program, Consumable.Ignore.IGNORE_COMMENT, Consumable.Ignore.IGNORE_H_SPACE);
    }

}
