package org.parser.examples.alpha;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.parser.Consumable;
import org.parser.tree.AST;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            System.out.println(args[0]);
            String programStr = loadFile(args[0]);
            Program program = parse(programStr);
            World world = instantiateWorld(program);
            goThroughNormal(world);
            printResult(world);
        }
    }

    private static void printResult(World world) {
        System.out.println(world);
    }

    private static void goThroughNormal(World world) {
        world.evalProgram();
    }

    private static World instantiateWorld(Program program) {
        return new World(program);
    }

    private static Program parse(String program) { // exception
        var alphaParser = new AlphaNotationParser();
        // \s ist falsch
        Consumable consProgram = new Consumable(program,
                Consumable.Ignore.IGNORE_H_SPACE,
                Consumable.Ignore.IGNORE_COMMENT
        );
        var optionalAST = alphaParser.applyTo(consProgram);
        System.out.println(optionalAST);
        return new Program(optionalAST.orElseGet(() -> new AST<>(Type.PROGRAM)));
    }

    private static String loadFile(String filename) throws IOException { // exception
        if (checkFileName(filename)) {
            return FileUtils.readFileToString(new File(filename), StandardCharsets.UTF_8);
        }
        return "";
    }

    private static boolean checkFileName(String filename) { // exception
        File to_interpret = new File(filename);
        return FilenameUtils.isExtension(filename, "alph")
                && to_interpret.exists() && !to_interpret.isDirectory();
    }
}
