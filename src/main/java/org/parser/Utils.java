package org.parser;

import java.util.Optional;

public class Utils {
    /**
     *
     * @param t Objekt
     * @return Konvertiert ein Objekt vom Typ T zu Optional<T>. Hierbei wird Optional.empty() zurückgegeben, falls
     *         das Objekt null ist und ansonsten Optional.of(t)
     * @param <T> Typ des Objekts
     */
    public static <T> Optional<T> convertToOptional(T t) {
        return t != null ? Optional.of(t) : Optional.empty();
    }
}
