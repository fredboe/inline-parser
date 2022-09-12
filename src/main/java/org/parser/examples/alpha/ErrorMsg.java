package org.parser.examples.alpha;

import org.parser.Consumable;

public class ErrorMsg extends Exception {
    public ErrorMsg(String error, String where) {
        super(error + "  At: " + where);
    }

    public static void throwParsingError(Consumable consumable) throws ErrorMsg {
        int size = consumable.getSequenceLeft().length();
        String where = consumable.getSequenceLeft()
                .subSequence(0, Math.min(100, size)).toString();
        throw new ErrorMsg("Parsing error occured.", where);
    }

    public static void throwEmptyStack(int line) throws ErrorMsg {
        throw new ErrorMsg("The system tried to call pop on an empty stack.", String.valueOf(line));
    }
}
