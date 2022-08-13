package org.parser;

import java.util.Optional;

public class Utils {
    public static <T> Optional<T> convertToOptional(T t) {
        return t != null ? Optional.of(t) : Optional.empty();
    }
}
