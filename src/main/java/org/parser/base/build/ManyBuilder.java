package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.ManyParser;
import org.parser.base.Parser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Der ManyBuilder ermöglicht das Bauen einer Many-Subrule (also ein Parser der so lange ausgeführt wird,
 * bis er fehlschlägt).
 * @param <TYPE> Typ des AST
 * @param <ANNOTATION> Annotation des AST
 */
public class ManyBuilder<TYPE, ANNOTATION> {
    /**
     * Der ParserBuilder wird verwendet, um neue PlaceholderParser zu erhalten
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * Der ConcatRuleBuilder wird verwendet, um beim Aufrufen von manyEnd() den entstandenen Many-Ausdruck
     * in der Concat-Rule zu speichern
     */
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder;
    /**
     * Der Parser, der mit many gewrappt wird.
     */
    private ConcatParser<TYPE, ANNOTATION> concatParser;
    /**
     * Gibt an, ob der ManyBuilder noch weitere Methodenaufrufe erlaubt
     */
    private boolean frozen;

    public ManyBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder,
                       ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder) {
        this.parserBuilder = parserBuilder;
        this.concatBuilder = concatBuilder;
        this.concatParser  = Parser.concat(null, new ArrayList<>());
        this.frozen = true;
    }

    /**
     * Erzeugt eine neue Many-Rule, wenn die alte bereits eingefroren war.
     */
    void newManyRule() {
        if (frozen) {
            concatParser = Parser.concat(null, new ArrayList<>());
            frozen = false;
        }
    }

    /**
     * Fügt der aktuellen Many-Rule als neuen Schritt einen Hide-Parser ein.
     * @param pattern Pattern
     * @return Der zugrundeliegende ManyBuilder.
     */
    public ManyBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    /**
     * Fügt der aktuellen Many-Rule als neuen Schritt einen Hide-Parser ein.
     * @param regex RegEx
     * @return Der zugrundeliegende ManyBuilder.
     */
    public ManyBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    /**
     * Fügt der aktuellen Many-Rule als neuen Schritt einen Placeholder-Parser, der die Regel mit dem übergebenen
     * Namen abbilden wird, ein.
     * @param name Name der Rule
     * @return Der zugrundeliegende ManyBuilder.
     */
    public ManyBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    /**
     * Friert diesen ManyBuilder ein und speichert den Many-Ausdruck im ConcatRuleBuilder.
     * @return Gibt den ConcatRuleBuilder zurück, in dem der Many-Ausdruck abgespeichert wurde.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> manyEnd() {
        frozen = true;
        if (!concatParser.isEmpty()) concatBuilder.addStep(new ManyParser<>(null, concatParser));
        return concatBuilder;
    }

    /**
     * Fügt dem aktuellen Many-Ausdruck einen neuen Schritt ein, falls dieser ManyBuilder nicht eingefroren ist.
     * @param parser Parser
     * @return Der zugrundeliegende ManyBuilder.
     */
    private ManyBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (parser != null && !frozen) concatParser.addSubparser(parser);
        return this;
    }
}
