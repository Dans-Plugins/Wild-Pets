package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Abstract base class for plugin commands.
 */
public abstract class AbstractPluginCommand {
    private final ArrayList<String> names;
    private final ArrayList<String> permissions;

    public AbstractPluginCommand(ArrayList<String> names, ArrayList<String> permissions) {
        this.names = names;
        this.permissions = permissions;
    }

    /**
     * Method to execute the command with no arguments.
     * @param sender The sender of the command.
     * @return Whether the execution of the command was successful.
     */
    public abstract boolean execute(CommandSender sender);

    /**
     * @param sender The sender of the command.
     * @param args The arguments of the command.
     * @return Whether the execution of the command was successful.
     */
    public abstract boolean execute(CommandSender sender, String[] args);

    /**
     * @param message to send.
     * @param args to check.
     * @param sender to send message to.
     * @param color of the message.
     * @return Boolean signifying whether there were no arguments.
     */
    public boolean sendMessageIfNoArguments(String message, String[] args, CommandSender sender, ChatColor color) {
        if (args.length == 0) {
            sender.sendMessage(color + message);
            return true;
        }
        return false;
    }

    /**
     * @param line to convert into an Integer.
     * @param orElse if the conversion fails.
     * @return {@link Integer} numeric.
     */
    public int getIntSafe(String line, int orElse) {
        try {
            return Integer.parseInt(line);
        } catch (Exception e) {
            return orElse;
        }
    }

    /**
     * Method to test if something matches any goal string.
     *
     * @param matchCase for the comparison (or not)
     * @param what to test
     * @param goals to compare with
     * @return {@code true} if something in goals matches what.
     */
    public boolean safeEquals(boolean matchCase, String what, String... goals) {
        return Arrays.stream(goals).anyMatch(goal ->
                matchCase && goal.equals(what) || !matchCase && goal.equalsIgnoreCase(what)
        );
    }

    /**
     * @return A list of names of the command.
     */
    public ArrayList<String> getNames() {
        return names;
    }

    /**
     * @return A list of permissions of the command.
     */
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public ArrayList<String> extractArgumentsInsideDoubleQuotes(String[] args) throws Exception {
        ArgumentParser argumentParser = new ArgumentParser();
        ArrayList<String> doubleQuoteArgs = argumentParser.getArgumentsInsideDoubleQuotes(args);
        if (doubleQuoteArgs.size() < 2) {
            throw new Exception();
        }
        return doubleQuoteArgs;
    }
}
