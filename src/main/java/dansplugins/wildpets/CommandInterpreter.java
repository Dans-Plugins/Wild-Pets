package dansplugins.wildpets;

import dansplugins.wildpets.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("wildpets") || label.equalsIgnoreCase("wp")) {

            if (args.length == 0) {
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = getArguments(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                HelpCommand command = new HelpCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("tame")) {
                TameCommand command = new TameCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("select")) {
                SelectCommand command = new SelectCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("rename")) {
                RenameCommand command = new RenameCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("info")) {
                InfoCommand command = new InfoCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("stay")) {
                StayCommand command = new StayCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("wander")) {
                WanderCommand command = new WanderCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("list")) {
                ListCommand command = new ListCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("call")) {
                CallCommand command = new CallCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("setfree")) {
                SetFreeCommand command = new SetFreeCommand();
                return command.execute(sender);
            }

        }

        sender.sendMessage(ChatColor.RED + "Wild Pets doesn't recognize that command.");
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
