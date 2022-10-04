package org.parser.alpha;

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
        do {
            String input = enterLine();
            if (input.equalsIgnoreCase("end")) break;
            processLine(input);
        } while(world.getPc() >= 0);
    }

    private void processLine(String input) {
        try {
            if (!changeMode(input)) {
                world.addLine(input);
                mode.accept(world);
            }
        } catch (AlphaError e) {
            IO.error(e);
        }
    }

    private void description() {
        IO.info("""
                This is a command line for the pseudo assembler 'alpha notation'.
                To see how alpha notation works please read the README.

                Keywords:
                This command line can be stopped with 'end'.
                To create a new subroutine you need to go into subroutine-mode (enter code without executing it).
                To go into subroutine-mode enter 'subroutine' and to leave subroutine-mode just enter a blank line.""");
    }

    private String enterLine() {
        return IO.enterLine(mode.getPrintBefore());
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
