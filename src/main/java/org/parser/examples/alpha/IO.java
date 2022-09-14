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

    public static String enterLine() {
        System.out.print(startOfInputLine);
        return input.nextLine();
    }

    public static void info(Value value) {
        info(value.toString());
    }

    public static void info(String msg) {
        System.out.println(msg);
    }

    public static void error(Exception e) {
        error(e.getMessage());
    }

    public static void error(String error) {
        System.err.println(error);
        System.err.println();
    }

    public static World loadProgram(String filename) throws AlphaError {
        return new World(new AlphaProgram(loadFile(filename)));
    }

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
