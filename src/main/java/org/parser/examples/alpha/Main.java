package org.parser.examples.alpha;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.parser.Consumable;
import org.parser.tree.AST;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            process(args);
        } catch (ErrorMsg e) {
            System.err.println(e.getMessage());
        }
    }

    public static void process(String[] args) throws ErrorMsg, IOException {
        if (args.length == 0) {
            cli();
        } else {
            World world = prepareProgram(args[0]);
            if (args.length >= 2 && isLbl(args[1])) {
                goThroughLineByLine(world);
            } else {
                goThroughNormal(world);
            }
        }
    }

    private static void cli() {

    }

    private static World prepareProgram(String filename) throws IOException, ErrorMsg {
        String programStr = loadFile(filename);
        Program program = parse(programStr);
        return instantiateWorld(program);
    }

    private static void goThroughLineByLine(World world) throws ErrorMsg {
        Scanner input = new Scanner(System.in);
        boolean shouldExecute = true;
        System.out.println("Press enter to execute the next line!");
        while (shouldExecute) {
            System.out.print(world.getPC());
            shouldExecute = world.executeNextLine();
            input.nextLine();
        }
        printResult(world);
    }

    private static void goThroughNormal(World world) throws ErrorMsg {
        world.evalProgram();
        printResult(world);
    }

    private static void printResult(World world) {
        System.out.println(world);
    }

    private static World instantiateWorld(Program program) {
        return new World(program);
    }

    private static Program parse(String program) throws ErrorMsg {
        var alphaParser = new AlphaNotationParser();
        Consumable consProgram = new Consumable(program,
                Consumable.Ignore.IGNORE_H_SPACE,
                Consumable.Ignore.IGNORE_COMMENT
        );
        var optionalAST = alphaParser.applyTo(consProgram);
        if (!consProgram.isEmpty()) ErrorMsg.throwParsingError(consProgram);
        return new Program(optionalAST.orElseGet(() -> new AST<>(Type.PROGRAM)));
    }

    private static String loadFile(String filename) throws IOException, ErrorMsg {
        checkFilename(filename);
        return FileUtils.readFileToString(new File(filename), StandardCharsets.UTF_8);
    }

    private static void checkFilename(String filename) throws ErrorMsg {
        File to_interpret = new File(filename);
        if (!FilenameUtils.isExtension(filename, "alph") || !to_interpret.exists()
                || to_interpret.isDirectory()) {
            ErrorMsg.throwWrongFilename(filename);
        }
    }

    private static boolean isLbl(String param) {
        return param.equals("-LineByLine") || param.equals("-lbl");
    }
}
