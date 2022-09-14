package org.parser.examples.alpha;

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

    /**
     *
     * @return Waits for the user to enter a new line and returns the input.
     */
    public static String enterLine() {
        System.out.print(startOfInputLine);
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
        System.err.println(error);
        System.out.println();
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
