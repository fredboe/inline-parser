package org.parser.base;

import org.parser.Consumable;
import org.parser.Utils;
import org.parser.tree.AST;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Or-Parser
 */
public class OrParser<TYPE, ANNOTATION> implements Parser<TYPE, ANNOTATION> {
    /**
     * Menge an Parsern, die verodert werden sollen.
     */
    private final List<Parser<TYPE, ANNOTATION>> parsers;
    /**
     * Diese Methode wird aufgerufen, sobald der erste Parser erfolgreich war. Ihr wird dann der gelieferte
     * AST übergeben. Diese Methode soll letztendlich dann den resultierenden AST liefern.
     */
    private final Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess;

    public OrParser(Function<AST<TYPE, ANNOTATION>, AST<TYPE, ANNOTATION>> atSuccess,
                    List<Parser<TYPE, ANNOTATION>> parsers) {
        this.atSuccess = atSuccess;
        this.parsers = parsers;
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
        for (Parser<TYPE, ANNOTATION> parser : parsers) {
            Optional<AST<TYPE, ANNOTATION>> optionalAST = parser.applyTo(consumable);
            if (optionalAST.isPresent()) {
                AST<TYPE, ANNOTATION> ast = optionalAST.get();
                return Utils.convertToOptional(
                        atSuccess.apply(ast).setIgnore(ast.shouldIgnore())
                );
            }
        }
        return Optional.empty();
    }
}
