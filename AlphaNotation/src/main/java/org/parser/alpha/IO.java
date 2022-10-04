package org.parser.alpha;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IO {
    private static final Scanner input = new Scanner(System.in);
    private static final String startOfInputLine = ">>> ";
    private static final String red = "\u001B[31m";
    private static final String resetColor = "\u001B[0m";


    /**
     *
     * @return Waits for the user to enter a new line and returns the input.
     */
    public static String enterLine() {
        return enterLine("");
    }

    /**
     *
     * @param printBefore What to print before
     * @return Prints a line prefix (">>> "), then printBefore and then waits for the user to enter a new line and returns the input.
     */
    public static String enterLine(String printBefore) {
        System.out.print(startOfInputLine);
        return enterLineWithoutPrefix(printBefore);
    }

    /**
     *
     * @param printBefore What to print before
     * @return Prints printBefore and then waits for the user to enter a new line and returns the input.
     */
    public static String enterLineWithoutPrefix(String printBefore) {
        System.out.print(printBefore);
        return input.nextLine();
    }

    /**
     * Prints the value to the console.
     * @param value Value
     */
    public static void info(Value value) {
        info(value.toString());
    }

    /**
     * Prints the message to the console.
     * @param msg Message
     */
    public static void info(String msg) {
        System.out.println(msg);
    }

    /**
     * Prints the exception to the console.
     * @param e Exception
     */
    public static void error(Exception e) {
        error(e.getMessage());
    }

    /**
     * Prints the error-message to the console.
     * @param error Error-message
     */
    public static void error(String error) {
        System.out.println(red + error + resetColor);
    }

    /**
     * Loads the file, then parses it and then creates a new world for this program.
     * @param filename Filename
     * @return Returns the new world for this file/program.
     * @throws AlphaError at parsing failure.
     */
    public static World loadProgram(String filename) throws AlphaError {
        return new World(new Program(loadFile(filename)));
    }

    /**
     *
     * @param filename Filename
     * @return Reads the file and returns a list of each line.
     */
    public static List<String> loadFile(String filename) {
        if (filename == null || filename.equals("")) return new ArrayList<>();
        try {
            return FileUtils.readLines(new File(filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            error(e);
            return new ArrayList<>();
        }
    }
}
