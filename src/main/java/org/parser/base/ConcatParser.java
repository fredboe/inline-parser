package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Concatenation-Parser
 * Regeln:
 * - Die atSuccess Funktion muss auf jeden Fall auch den Fall beachten, wenn die übergebene Liste leer ist.
 */
public class ConcatParser<TYPE, ANNOTATION> implements WithSubparsersParser<TYPE, ANNOTATION> {
    /**
     * Menge von Parsern, die hintereinander gefügt werden sollen (Reihenfolge ist wichtig)
     */
    private List<Parser<TYPE, ANNOTATION>> parsers;
    /**
     * Diese Funktion wird aufgerufen, wenn alle Parser in der Parser-Liste einen erfolgreichen AST
     * geliefert haben. Die Liste der gelieferten ASTs (ohne die ignored-ASTs) wird dann an diese Methode
     * weitergegeben. Diese Methode soll letztendlich dann den resultierenden AST liefern.
     */
    private final Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess;

    public ConcatParser(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess) {
        this.atSuccess = atSuccess;
        this.parsers = new ArrayList<>();
    }

    public ConcatParser(Function<List<AST<TYPE, ANNOTATION>>, AST<TYPE, ANNOTATION>> atSuccess,
                        List<Parser<TYPE, ANNOTATION>> parsers) {
        this(atSuccess);
        this.parsers = parsers;
    }

    /**
     * Wendet alle Parser nacheinander auf das Consumable Objekt an. Die Methode liefert nur einen erfolgreichen
     * AST, wenn alle Parser erfolgreich waren. Für den resultierenden AST wird dann atSuccess aufgerufen.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (empty, falls einer der Parser einen Fehler liefert)
     */
    @Override
    public Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable) {
        Consumable copy = new Consumable(consumable); // beim Fehlschlag soll nichts konsumiert werden
        List<AST<TYPE, ANNOTATION>> ASTrees = new ArrayList<>(parsers.size());
        for (Parser<TYPE, ANNOTATION> parser : parsers) {
            Optional<AST<TYPE, ANNOTATION>> tree = parser.applyTo(consumable);
            if (tree.isEmpty()) {
                consumable.resetTo(copy);
                return Optional.empty();
            }
            if (!tree.get().shouldIgnore()) ASTrees.add(tree.get());
        }
        return Optional.ofNullable(atSuccess.apply(ASTrees));
    }

    /**
     * Setzt die Subparser-Liste auf die übergebene Liste
     * @param parsers Subparser-Liste
     */
    @Override
    public void setSubparsers(List<Parser<TYPE, ANNOTATION>> parsers) {
        this.parsers = parsers;
    }
}