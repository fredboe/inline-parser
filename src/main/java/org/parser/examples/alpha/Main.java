package org.parser.examples.alpha;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            CLI cli = getCLI(args);
            cli.run();
        } catch (AlphaError e) {
            System.err.println(e.getMessage());
        }
    }

    private static CLI getCLI(String[] args) throws AlphaError, IOException {
        if (args.length > 0)
            return new CLI(loadFile(args[0]), isLbl(getFromArgs(1, args)));
        return new CLI(CLI.Mode.INTERFACE);
    }

    private static List<String> loadFile(String filename) throws IOException, AlphaError {
        if (filename == null || filename.equals("")) return new ArrayList<>();
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

    private static CLI.Mode isLbl(String param) {
        if (param.equals("-LineByLine") || param.equals("-lbl")) return CLI.Mode.LINE_BY_LINE;
        return CLI.Mode.IMMEDIATE;
    }

    private static String getFromArgs(int i, String[] args) {
        if (i < 0 || i >= args.length) return "";
        return args[i];
    }
}
