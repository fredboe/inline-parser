package org.parser;

import org.parser.tree.AST;

import java.util.Optional;

/**
 * If a parser was successful is determined by the match object of the returned AST or when its null.
 * Später richtiges Fail und so implementieren. -> Ist bis jetzt noch falsch
 *
 * null checks erweitern
 */

/**
 * Ein Parser sollte nur die consumable konsumieren, wenn der Parser erfolgreich ist.
 * @param <TYPE> Typ
 * @param <ANNOTATION> ANNOTATION-Class bei Abstract Syntax Tree
 */
public interface Parser<TYPE, ANNOTATION> {
    Optional<AST<TYPE, ANNOTATION>> applyTo(Consumable consumable);
}
