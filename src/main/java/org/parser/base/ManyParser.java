package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Ein Many-Parser hält einen Parser und führt diesen so lange aus, bis dieser fehlschlägt.
 * Ein Many-Parser ist immer erfolgreich, gibt also immer einen AST zurück.
 * @param <TYPE> Typ-Klasse des AST
 * @param <ANNOTATION> Annotation-Klasse des AST
 */
public class ManyParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    /**
     * Typ eines mit Many erstellten AST
     */
    private final TYPE type;
    /**
     * Parser der wiederholt ausgeführt werden soll
     */
    private Parser<TYPE, ANNOTATION> parser;

    public ManyParser(TYPE type) {
        this.type = type;
        this.parser = null;
    }

    public ManyParser(TYPE type, Parser<TYPE, ANNOTATION> parser) {
        this.type = type;
        this.parser = parser;
    }

    /**
     * Bei einem Many-Parser wird der gespeicherte Parser so lange ausgeführt, bis dieser fehlschlägt.
     * Am Ende wird dann ein AST erstellt, mit den beim mehrmaligen Ausführen des Parsers entstandenen ASTs als
     * Kindern (die Kinder-Liste kann also auch leer sein) und dem gespeicherten Typen.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (bei Many ist dieser immer present).
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Optional<AST<TYPE, ANNOTATION>> optionalAST;
        List<AST<TYPE, ANNOTATION>> ASTs = new ArrayList<>();

        while ((optionalAST = parser.applyTo(consumable)).isPresent()) {
            ASTs.add(optionalAST.get());
        }
        return Optional.of(new AST<>(type, null, ASTs));
    }

    public void setParserIfNull(Parser<TYPE, ANNOTATION> parser) {
        if (this.parser == null) this.parser = parser;
    }
}
