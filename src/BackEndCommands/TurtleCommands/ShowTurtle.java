package BackEndCommands.TurtleCommands;

import java.util.List;

import BackEndCommands.TurtleCommand;

/**
 * Executes the ShowTurtle Command
 * @author ezra
 *
 */
public class ShowTurtle extends TurtleCommand {
	private static final int ARGS = 0;

	/**
	 * Sets imageView visible property to true
	 * Returns 1
	 */
	@Override
	public double executeCommand(List<Double> args) {
		properties.getImageVisibleProperty().set(true);
		return 1;
	}

	@Override
	public int numArguments() {
		return ARGS;
	}
}