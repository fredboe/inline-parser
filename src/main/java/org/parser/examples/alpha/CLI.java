package org.parser.examples.alpha;

import org.parser.ThrowableConsumer;

public class CLI implements Runnable {
    private enum Mode {
        NORMAL(world -> {
            world.gotoLastLine();
            world.executeProgram();
        }, ""),
        SUBROUTINE(world -> {}, "subr ");

        private final ThrowableConsumer<World, AlphaError> transformer;
        private final String printBefore;

        Mode(ThrowableConsumer<World, AlphaError> transformer, String printBefore) {
            this.transformer = transformer;
            this.printBefore = printBefore;
        }

        public void accept(World world) throws AlphaError {
            transformer.accept(world);
        }

        public String getPrintBefore() {
            return printBefore;
        }
    }

    private final World world;
    private Mode mode;

    public CLI() throws AlphaError {
        world = new World(new Program());
        mode  = Mode.NORMAL;
    }

    public CLI(String filename) throws AlphaError {
        this();
        String executionLine = "exe " + filename;
        IO.info(executionLine);
        world.addLine(executionLine);
        world.executeNextLine();
    }

    @Override
    public void run() {
        description();
        while (world.getPc() >= 0) {
            newLine();
        }
    }

    private void newLine() {
        try {
            String input = IO.enterLine(mode.getPrintBefore());
            if (!changeMode(input)) {
                world.addLine(input);
                mode.accept(world);
            }
        } catch (AlphaError e) {
            IO.error(e);
        }
    }

    private void description() {
        IO.info("CLI");
    }

    private boolean changeMode(String input) {
        if(input.equalsIgnoreCase("subroutine")) {
            mode = Mode.SUBROUTINE;
            return true;
        } else if (input.equals("")) {
            mode = Mode.NORMAL;
            return true;
        }
        return false;
    }
}
