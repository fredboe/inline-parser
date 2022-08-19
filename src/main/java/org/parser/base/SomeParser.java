package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

/**
 * Ein Some-Parser hält einen Parser und führt diesen so lange aus, bis dieser fehlschlägt.
 * Ein Some-Parser ist erfolgreich, wenn der Parser mindestens einmal erfolgreich ist.
 * @param <TYPE> Typ-Klasse des AST
 * @param <ANNOTATION> Annotation-Klasse des AST
 */
public class SomeParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    /**
     * Typ eines mit Many erstellten AST
     */
    private final TYPE type;
    /**
     * Parser der wiederholt ausgeführt werden soll
     */
    private Parser<TYPE, ANNOTATION> parser;

    public SomeParser(TYPE type) {
        this.type = type;
        this.parser = null;
    }

    public SomeParser(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        this.type = type;
        this.parser = parser;
    }

    /**
     * Bei einem Some-Parser wird der gespeicherte Parser zunächst einmal ausgeführt. Dabei muss er erfolgreich
     * sein, ansonsten schlägt auch der Some-Parser fehl. Danach wird der gespeicherte Parser so lange ausgeführt,
     * bis dieser fehlschlägt. Am Ende wird dann ein AST erstellt, mit den beim mehrmaligen Ausführen des Parsers
     * entstandenen ASTs als Kindern (die Liste ist also mindestens einelementig) und dem gespeicherten Typen.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (bei Many ist dieser immer present).
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        ConcatParser<TYPE, ANNOTATION> someParser = new ConcatParser<>(trees -> trees.get(1).addChild(trees.get(0)));
        someParser.addSubparser(parser);
        someParser.addSubparser(new ManyParser<>(type, parser));
        return someParser.applyTo(consumable);
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
