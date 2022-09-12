package org.parser.examples.alpha;

import org.parser.Consumable;

public class AlphaError extends Exception {
    public AlphaError(String error, String where) {
        super(error + "  At: " + where);
    }

    public static void throwParsingError(Consumable consumable) throws AlphaError {
        int size = consumable.getSequenceLeft().length();
        String where = consumable.getSequenceLeft()
                .subSequence(0, Math.min(100, size)).toString();
        throw new AlphaError("Parsing error occurred.", where);
    }

    public static void throwEmptyStack(int line) throws AlphaError {
        throw new AlphaError("The system tried to call pop on an empty stack.", String.valueOf(line));
    }

    public static void throwWrongFilename(String filename) throws AlphaError {
        throw new AlphaError("The system was given a wrong filename " +
                "(the file must exist and the filename must end with 'alph')", filename);
    }
}
