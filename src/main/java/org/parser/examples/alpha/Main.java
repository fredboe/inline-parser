package org.parser.examples.alpha;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.parser.Consumable;
import org.parser.tree.AST;

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
        World world = prepareProgram(args[0]);
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

    private static void repeatedMemoryInfo(World world) throws AlphaError {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("Please enter 'mem' to see the whole memory, 'clear' to clear the memory," +
                "'end' to end the whole process or some VALUE (accumulator, address, constant or label).");
        do {
            System.out.print(" >>> ");
            input = scanner.nextLine();
            Value value = memoryInfo(input, world);
            if (value != null) System.out.println(value);
        } while (!input.equals("end"));
    }

    private static Value memoryInfo(String input, World world) throws AlphaError {
        if (!memKeys(input, world)) {
            return valueInfo(input, world);
        }
        return null;
    }

    private static Value valueInfo(String input, World world) throws AlphaError {
        var valueParser = AlphaProgram.alphaPool.getParser("VALUE");
        Consumable consInput = new Consumable(input);
        var optionalParsedValue = valueParser.applyTo(consInput);
        if (optionalParsedValue.isEmpty() || !consInput.isEmpty()) AlphaError.throwParsingError(consInput);
        world.evalAST(optionalParsedValue.orElseGet(() -> new AST<>(Type.PROGRAM)));
        return world.pop();
    }

    private static boolean memKeys(String input, World world) {
        String low_input = input.toLowerCase();
        switch (low_input) {
            case "mem" -> {
                printResult(world);
                return true;
            }
            case "clear" -> {
                world.clear();
                System.out.println("World has been cleared!");
                return true;
            }
        }
        return false;
    }

    private static void printResult(World world) {
        System.out.println(world);
    }

    private static World prepareProgram(String filename) throws IOException, AlphaError {
        List<String> programLines = loadFile(filename);
        AlphaProgram program = new AlphaProgram(programLines);
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
            AlphaError.throwWrongFilename(filename);
        }
    }

    private static boolean isLbl(String param) {
        return param.equals("-LineByLine") || param.equals("-lbl");
    }
}
