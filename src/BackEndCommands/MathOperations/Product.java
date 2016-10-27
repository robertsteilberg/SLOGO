package BackEndCommands.MathOperations;

import java.util.List;

import BackEndInternalAPI.Command;
import BackEndInternalAPI.ParseTreeNode;


/**
 * Executes the Product Command
 * @author ezra
 *
 */
public class Product implements Command {

	private static final int ARGS = 2;
	
	/**
	 * Returns the two arguments multiplied by each other
	 */
	@Override
	public double executeCommand(List<ParseTreeNode> args) {
        ParseTreeNode arg1 = args.get(0);
        ParseTreeNode arg2 = args.get(1);
        return arg1.executeCommand(arg1.getChildren()) * arg2.executeCommand(arg2.getChildren());
	}

    @Override
    public int numArguments() {
        return ARGS;
    }
}
