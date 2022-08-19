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
     * entstandenen ASTs als Kindern (die Liste ist also mindestens einelementig) und dem gespeicherten Typen. <br>
     *
     * Funktionsweise: Es wird ein Concat-Parser erzeugt, der aus dem normalen Parser und einem Many-Parser
     * des normalen Parsers besteht. Das Ergebnis ergibt sich dann aus dem ersten Durchlauf des Parsers
     * und dann aus den im Many-Parser erfolgreichen Durchläufen, also den Kindern des Many-Parsers.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (bei Many ist dieser immer present).
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        // Es wird ein Concat-Parser erzeugt, der aus dem normalen Parser und einem Many-Parser des normalen Parsers besteht.
        // Das Ergebnis ergibt sich dann aus dem ersten Durchlauf des Parsers und dann aus den im Many-Parser
        // erfolgreichen Durchläufen, also den Kindern des Many-Parsers.
        ConcatParser<TYPE, ANNOTATION> someParser = new ConcatParser<>(
                trees -> new AST<TYPE, ANNOTATION>(type, null).addChild(trees.get(0)).addChildren(trees.get(1).getChildren())
        );
        someParser.addSubparser(parser);
        someParser.addSubparser(new ManyParser<>(type, parser));
        return someParser.applyTo(consumable);
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
