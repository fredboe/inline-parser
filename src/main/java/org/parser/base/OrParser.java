package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Or-Parser
 */
public class OrParser<TYPE, ANNOTATION> implements DepthParser<TYPE, ANNOTATION> {
    /**
     * Menge an Parsern, die verodert werden sollen.
     */
    private List<Parser<TYPE, ANNOTATION>> parsers;
    /**
     * Diese Methode wird aufgerufen, sobald der erste Parser erfolgreich war. Ihr wird dann der gelieferte
     * AST übergeben. Diese Methode soll letztendlich dann den resultierenden AST liefern.
     */
    private final Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess;

    public OrParser(Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess) {
        this.atSuccess = atSuccess != null ? atSuccess : Parser.basicOrAtSuccess();
        this.parsers = new ArrayList<>();
    }

    public OrParser(Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess,
                    List<Parser<TYPE, ANNOTATION>> parsers) {
        this(atSuccess);
        if (parsers != null) this.parsers = parsers;
    }

    /**
     * Die Methode geht durch alle Parser durch und sobald der erste Parser erfolgreich auf dem Consumable
     * war, wird die Methode atSuccess aufgerufen. Zum Schluss wird das ignore-Bit dann noch auf das ignore-Bit
     * des erfolgreichen ASTs gesetzt.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (empty, falls alle der Parser einen Fehler liefern)
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Optional<AST<TYPE, ANNOTATION>> optionalAST = parsers.stream()
                .map(parser -> parser.applyTo(consumable))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        return optionalAST.map(ast -> atSuccess.apply(ast).setIgnore(ast.shouldIgnore()));
    }

    @Override
    public void addSubparser(Parser<TYPE, ANNOTATION> subparser) {
        if (subparser != null) parsers.add(subparser);
    }

    @Override
    public boolean isEmpty() {
        return parsers.isEmpty();
    }
}
