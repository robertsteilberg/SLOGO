package BackEndCommands.DisplayCommands;

import BackEndCommands.DisplayCommand;
import BackEndInternalAPI.ParseTreeNode;

/**
 * @author ezra
 */
public class PenColor extends DisplayCommand {
    private static final int ARGS = 0;

    /**
     * Gets the pen color from the display
     */
    @Override
    public double executeCommand(ParseTreeNode node) {
        return properties.getPenColor();
    }

    @Override
    public int numArguments() {
        return ARGS;
    }
}
