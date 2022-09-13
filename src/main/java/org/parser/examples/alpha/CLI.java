package org.parser.examples.alpha;

import org.parser.ThrowableConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI implements Runnable {
    public enum Mode {
        IMMEDIATE(CLI::immediateProcess), LINE_BY_LINE(CLI::lblProcess), INTERFACE(CLI::interfaceProcess);

        private final ThrowableConsumer<World, AlphaError> process;

        Mode(ThrowableConsumer<World, AlphaError> process) {
            this.process = process;
        }

        public void accept(World world) throws AlphaError {
            process.accept(world);
        }
    }

    private static final String lineBegin = ">>> ";

    private final Mode mode;
    private final World world;
    private final static Scanner inputScanner = new Scanner(System.in);


    public CLI(Mode mode) throws AlphaError {
        this(new ArrayList<>(), mode);
    }

    public CLI(List<String> lines, Mode mode) throws AlphaError {
        this.mode = mode;
        this.world = new World(new AlphaProgram(lines));
    }

    @Override
    public void run() {
        try {
            mode.accept(world);
        } catch (AlphaError e) {
            System.err.println(e.getMessage());
        }
    }

    private static void immediateProcess(World world) throws AlphaError {
        collapsingProcess(World::executeProgram, world);
    }

    private static void lblProcess(World world) throws AlphaError {
        collapsingProcess(w -> {
            // some prior explanation
            System.out.println("Press enter to execute the next line!");
            while (world.pcInBounds()) {
                nextLine(String.format("%1$3d", w.getPc()) + "     " + w.getCurrentLine());
                world.executeNextLine();
            }
        }, world);
    }

    private static void interfaceProcess(World world) throws AlphaError {
        String input;
        while (!(input = nextLine()).equals("end")) {
            if (input.equalsIgnoreCase("program")) {
                // allow the user to input an entire program (blank line end this mode)
            } else {
                if (!addLineAndExecute(world, input)) {
                    System.out.println(memoryInfo(world, input));
                }
            }
        }
    }

    private static void collapsingProcess(ThrowableConsumer<World, AlphaError> process, World world) throws AlphaError {
        process.accept(world);
        repeatedMemoryInfo(world);
    }

    private static boolean addLineAndExecute(World world, String line) throws AlphaError {
        if (world.addLine(line)) {
            world.executeProgram(true);
            return true;
        }
        return false;
    }

    private static void repeatedMemoryInfo(World world) throws AlphaError {
        System.out.println("Please enter 'mem' to see the whole memory, 'clear' to clear the memory," +
                "'end' to end the whole process or some VALUE (accumulator, address, constant or label).");
        String input;
        while (!(input = nextLine()).equals("end")) {
            String info  = memoryInfo(world, input);
            System.out.println(info);
        }
    }

    private static String memoryInfo(World world, String input) throws AlphaError {
        return "".equals(memKeys(world, input))
                ? valueInfo(world, input)
                : memKeys(world, input);
    }

    private static String valueInfo(World world, String input) throws AlphaError {
        var parsedValue = AlphaProgram.parseLine(AlphaProgram.valueParser, input);
        world.evalAST(parsedValue);
        return world.pop().toString();
    }

    private static String memKeys(World world, String input) {
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

    private static String nextLine() {
        return nextLine("");
    }

    private static String nextLine(String to_print) {
        System.out.print(lineBegin);
        System.out.print(to_print);
        return inputScanner.nextLine();
    }
}
