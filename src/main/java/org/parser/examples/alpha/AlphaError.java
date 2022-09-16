package org.parser.examples.alpha;

import org.apache.commons.lang3.StringUtils;
import org.parser.Consumable;
import org.parser.tree.AST;

public class AlphaError extends Exception {
    public AlphaError(String error, String where) {
        super(error + "  At: " + where);
    }


    public static class ParsingException extends AlphaError {
        private static final String errorMsg = "Parsing error occurred.";
        private static final int maxWidth = 100;

        public ParsingException(Consumable consumable) {
            this(StringUtils.abbreviate(consumable.getSequenceLeft().toString(), maxWidth));
        }

        public ParsingException(String where) {
            super(errorMsg, where);
        }
    }

    public static class EmptyStackException extends AlphaError {
        private static final String errorMsg = "The system tried to call pop on an empty stack.";

        public EmptyStackException(int line) {
            this(String.valueOf(line));
        }

        public EmptyStackException(String where) {
            super(errorMsg, where);
        }
    }

    public static class NullOccurredException extends AlphaError {
        private static final String errorMsg = "The system tried to process 'null' (fatal error).";

        public NullOccurredException(AST<Type> ast) {
            this(ast.toString());
        }

        public NullOccurredException(String where) {
            super(errorMsg, where);
        }
    }
}
