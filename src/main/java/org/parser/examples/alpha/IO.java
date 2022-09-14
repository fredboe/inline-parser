package org.parser.examples.alpha;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IO {
    private static final Scanner input = new Scanner(System.in);
    private static final String startOfLine = ">>> ";

    public static String enterLine() {
        System.out.print(startOfLine);
        return input.nextLine();
    }

    public static void info(String msg) {
        System.out.println(msg);
    }

    public static void info(Value value) {
        System.out.println(value);
    }

    public static void error(Exception e) {
        error(e.getMessage());
    }

    public static void error(String error) {
        System.err.println(error);
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
