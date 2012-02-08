package name.pilgr.android.picat.model;

import name.pilgr.android.picat.shared.ExecutableCommand;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 10.07.11
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class Command {
    private String command;

    public Command(String command_) {
        command = command_;
    }

    public ExecutableCommand getExecutableCommand() {
        return new ExecutableCommand(command);
    }
}
