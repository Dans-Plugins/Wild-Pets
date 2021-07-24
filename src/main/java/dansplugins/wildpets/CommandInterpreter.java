package dansplugins.wildpets;

import dansplugins.wildpets.commands.*;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("wildpets") || label.equalsIgnoreCase("wp")) {

            if (args.length == 0) {
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = getArguments(args);

            if (secondaryLabel.equalsIgnoreCase("tame")) {
                TameCommand command = new TameCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("select")) {
                SelectCommand command = new SelectCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("rename")) {
                RenameCommand command = new RenameCommand();
                command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("info")) {
                InfoCommand command = new InfoCommand();
                command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("stay")) {
                StayCommand command = new StayCommand();
                command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("wander")) {
                WanderCommand command = new WanderCommand();
                command.execute(sender);
            }

        }

        return false;
    }

    private String[] getArguments(String[] args) {
        String[] toReturn = new String[args.length - 1];

        for (int i = 1; i < args.length; i++) {
            toReturn[i - 1] = args[i];
        }

        return toReturn;
    }

}
