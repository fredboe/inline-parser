package org.parser.examples.alpha;

public class CLI implements Runnable {
    private final World world;

    public CLI() throws AlphaError {
        world = new World(new AlphaProgram());
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
            try {
                String input = IO.enterLine();
                world.addLine(input);
                world.executeNextLine();
            } catch (AlphaError e) {
                IO.error(e);
            }
        }
    }

    private void description() {
        IO.info("Start");
    }
}
