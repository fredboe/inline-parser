package org.parser;

import org.parser.base.OrParser;
import org.parser.base.RegExParser;

import java.util.regex.Pattern;

public class Main {
    enum TYPE {
        NUMBER, DIGIT, DIGIT2
    }

    enum ANNOTATION {

    }

    public static void main(String[] args) {
        Parser<TYPE, ANNOTATION> parser = Parser.or(TYPE.NUMBER,
                Parser.match(TYPE.DIGIT2, Pattern.compile("\\d+\\.\\d*")),
                Parser.match(TYPE.DIGIT, Pattern.compile("\\d+"))
        );

        var test = parser.applyTo(new Consumable("123"));
        System.out.println("test");
    }
}
