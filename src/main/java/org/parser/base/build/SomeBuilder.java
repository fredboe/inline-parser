package org.parser.base.build;

import org.parser.base.ConcatParser;
import org.parser.base.ManyParser;
import org.parser.base.Parser;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Der SomeBuilder ermöglicht das Bauen einer Some-Subrule (also ein Parser der so lange ausgeführt wird,
 * bis er fehlschlägt, wobei dieser Parser mindestens einmal erfolgreich sein muss).
 * @param <TYPE> Typ des AST
 * @param <ANNOTATION> Annotation des AST
 */
public class SomeBuilder<TYPE, ANNOTATION> {
    /**
     * Der ParserBuilder wird verwendet, um neue PlaceholderParser zu erhalten
     */
    private final ParserBuilder<TYPE, ANNOTATION> parserBuilder;
    /**
     * Der ConcatRuleBuilder wird verwendet, um beim Aufrufen von someEnd() den entstandenen Some-Ausdruck
     * in der Concat-Rule zu speichern
     */
    private final ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder;
    /**
     * Der Parser, der mit some gewrappt wird.
     */
    private ConcatParser<TYPE, ANNOTATION> concatParser;
    /**
     * Gibt an, ob der SomeBuilder noch weitere Methodenaufrufe erlaubt
     */
    private boolean frozen;

    public SomeBuilder(ParserBuilder<TYPE, ANNOTATION> parserBuilder, ConcatRuleBuilder<TYPE, ANNOTATION> concatBuilder) {
        this.parserBuilder = parserBuilder;
        this.concatBuilder = concatBuilder;
        this.concatParser = Parser.concat(null, new ArrayList<>());
        this.frozen = true;
    }

    /**
     * Erzeugt eine neue Some-Rule, wenn die alte bereits eingefroren war.
     */
    void newSomeRule() {
        if (frozen) {
            concatParser = Parser.concat(null, new ArrayList<>());
            frozen = false;
        }
    }

    /**
     * Fügt der aktuellen Some-Rule als neuen Schritt einen Hide-Parser ein.
     * @param pattern Pattern
     * @return Der zugrundeliegende SomeBuilder.
     */
    public SomeBuilder<TYPE, ANNOTATION> match(Pattern pattern) {
        return addStep(Parser.hide(pattern));
    }

    /**
     * Fügt der aktuellen Some-Rule als neuen Schritt einen Hide-Parser ein.
     * @param regex RegEx
     * @return Der zugrundeliegende SomeBuilder.
     */
    public SomeBuilder<TYPE, ANNOTATION> match(String regex) {
        return match(parserBuilder.getPattern(regex));
    }

    /**
     * Fügt der aktuellen Some-Rule als neuen Schritt einen Placeholder-Parser, der die Regel mit dem übergebenen
     * Namen abbilden wird, ein.
     * @param name Name der Rule
     * @return Der zugrundeliegende SomeBuilder.
     */
    public SomeBuilder<TYPE, ANNOTATION> rule(String name) {
        return addStep(parserBuilder.getPlaceholder(name));
    }

    /**
     * Friert diesen SomeBuilder ein und speichert den Some-Ausdruck im ConcatRuleBuilder.
     * @return Gibt den ConcatRuleBuilder zurück, in dem der Some-Ausdruck abgespeichert wurde.
     */
    public ConcatRuleBuilder<TYPE, ANNOTATION> someEnd() {
        frozen = true;
        if (!concatParser.isEmpty()) {
            concatBuilder.addStep(concatParser);
            concatBuilder.addStep(new ManyParser<>(null, concatParser));
        }
        return concatBuilder;
    }

    /**
     * Fügt dem aktuellen Some-Ausdruck einen neuen Schritt ein, falls dieser SomeBuilder nicht eingefroren ist.
     * @param parser Parser
     * @return Der zugrundeliegende SomeBuilder.
     */
    private SomeBuilder<TYPE, ANNOTATION> addStep(Parser<TYPE, ANNOTATION> parser) {
        if (parser != null && !frozen) concatParser.addSubparser(parser);
        return this;
    }
}
