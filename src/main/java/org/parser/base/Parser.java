package org.parser.base;

import org.parser.Consumable;
import org.parser.tree.AST;

import java.util.Optional;

/**
 * Regeln:
 * - Ein Parser sollte das Consumable nur konsumieren, wenn der Parser erfolgreich ist.
 * - Wird ein Verbindungsparser (ASTs als Input) implementiert, sollten die ASTs ignoriert werden,
 *   bei denen das ignore-Bit gesetzt ist
 * - Ein Parsing-Fehler soll mittels Optional übergeben werden (empty)
 * @param <TYPE> Typ
 * @param <ANNOTATION> ANNOTATION-Class beim Abstract Syntax Tree
 */
public interface Parser<TYPE, ANNOTATION> {
    /**
     * Erhält eine CharSequence und erzeugt aus dieser einen AST.
     * @param consumable Consumable
     * @return Ein AST mit Optional gewrappt (empty falls Parsing-Fehler)
     */
    Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable);

    /**
     * Erhält eine CharSequence und erzeugt aus dieser einen AST.
     * @param sequence CharSequence
     * @return Ein AST mit Optional gewrappt (empty falls Parsing-Fehler)
     */
    default Optional<AST<TYPE, ANNOTATION>> applyTo(CharSequence sequence) {
        return applyTo(new Consumable(sequence));
    }
}
