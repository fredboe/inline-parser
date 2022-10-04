package org.parser.alpha;

public class AlphaNotation {
    public static void main(String[] args) {
        try {
            CLI cli = getCLI(args);
            cli.run();
        } catch (AlphaError e) {
            IO.error(e);
        }
    }

    private static CLI getCLI(String[] args) throws AlphaError {
        if (args.length > 0) return new CLI(args[0]);
        return new CLI();
    }
}
