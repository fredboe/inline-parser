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
            super(errorMsg, StringUtils.abbreviate(consumable.getSequenceLeft().toString(), maxWidth));
        }

        public ParsingException(String where) {
            super(errorMsg, where);
        }
    }

    public static class EmptyStackException extends AlphaError {
        private static final String errorMsg = "The system tried to call pop on an empty stack.";

        public EmptyStackException(int line) {
            super(errorMsg, String.valueOf(line));
        }

        public EmptyStackException(String where) {
            super(errorMsg, where);
        }
    }

    public static class WrongFilenameException extends AlphaError {
        private static final String errorMsg = "The system was given a wrong filename " +
                "(the file must exist and the filename must end with 'alph').";

        public WrongFilenameException(String filename) {
            super(errorMsg, filename);
        }
    }

    public static class NullOccurredException extends AlphaError {
        private static final String errorMsg = "The system tried to process 'null' (fatal error).";

        public NullOccurredException(AST<Type> ast) {
            super(errorMsg, ast.toString());
        }

        public NullOccurredException(String where) {
            super(errorMsg, where);
        }
    }
}
