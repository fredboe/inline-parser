package org.lexer;

import java.util.regex.Pattern;

public class CommonIgnores {
    public static Pattern IGNORE_H_SPACE = Pattern.compile("\\h");
    public static Pattern IGNORE_V_SPACE = Pattern.compile("\\v");
    public static Pattern IGNORE_WHITESPACE = Pattern.compile("\\s");
    public static Pattern IGNORE_LINEBREAK = Pattern.compile("\\R");
    public static Pattern IGNORE_COMMENT = Pattern.compile("//.*");
}
