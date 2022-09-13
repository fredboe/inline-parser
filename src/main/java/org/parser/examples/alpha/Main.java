package org.parser.examples.alpha;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            process(args);
        } catch (AlphaError e) {
            System.err.println(e.getMessage());
        }
    }

    public static void process(String[] args) throws AlphaError, IOException {
        if (args.length == 0) {
            cli();
        } else {
            processWithArgs(args);
        }
    }

    private static void processWithArgs(String[] args) throws AlphaError, IOException {
        List<String> lines = loadFile(args[0]);
        World world = prepareProgram(lines);
        if (args.length >= 2 && isLbl(args[1])) {
            goThroughLineByLine(world);
        } else {
            goThroughNormal(world);
        }
        repeatedMemoryInfo(world);
    }

    private static void cli() {

    }

    private static void goThroughLineByLine(World world) throws AlphaError {
        Scanner input = new Scanner(System.in);
        boolean shouldExecute = true;

        // some prior explanation

        System.out.println("Press enter to execute the next line!");
        while (shouldExecute && world.pcInBounds()) {
            System.out.print(">>> " + world.getPc() + "   " + world.getCurrentLine());
            shouldExecute = world.executeNextLine();
            input.nextLine();
        }
    }

    private static void goThroughNormal(World world) throws AlphaError {
        world.evalProgram();
    }

    private static void repeatedMemoryInfo(World world) {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Please enter 'mem' to see the whole memory, 'clear' to clear the memory," +
                "'end' to end the whole process or some VALUE (accumulator, address, constant or label).");
        do {
            System.out.print(">>> ");
            input = scanner.nextLine();
            try {
                String info = memoryInfo(input, world);
                System.out.println(info);
            } catch (AlphaError e) {
                System.err.println(e.getMessage());
                System.out.println();
            }
        } while (!input.equals("end"));
    }

    private static String memoryInfo(String input, World world) throws AlphaError {
        return memKeys(input, world).equals("")
                ? valueInfo(input, world)
                : memKeys(input, world);
    }

    private static String valueInfo(String input, World world) throws AlphaError {
        var parsedValue = AlphaProgram.parseLine(AlphaProgram.valueParser, input);
        world.evalAST(parsedValue);
        return world.pop().toString();
    }

    private static String memKeys(String input, World world) {
        String low_input = input.toLowerCase();
        switch (low_input) {
            case "mem" -> {
                return world.toString();
            }
            case "clear" -> {
                world.clear();
                return "Memory has been cleared!";
            }
        }
        return "";
    }

    private static void printResult(World world) {
        System.out.println(world);
    }

    private static World prepareProgram(List<String> lines) throws AlphaError {
        AlphaProgram program = new AlphaProgram(lines);
        return instantiateWorld(program);
    }

    private static World instantiateWorld(AlphaProgram program) {
        return new World(program);
    }

    private static List<String> loadFile(String filename) throws IOException, AlphaError {
        checkFilename(filename);
        return FileUtils.readLines(new File(filename), StandardCharsets.UTF_8);
    }

    private static void checkFilename(String filename) throws AlphaError {
        File to_interpret = new File(filename);
        if (!FilenameUtils.isExtension(filename, "alph") || !to_interpret.exists()
                || to_interpret.isDirectory()) {
            throw new AlphaError.WrongFilenameException(filename);
        }
    }

    private static boolean isLbl(String param) {
        return param.equals("-LineByLine") || param.equals("-lbl");
    }
}
