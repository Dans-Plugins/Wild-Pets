package dansplugins.wildpets;

import dansplugins.wildpets.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("wildpets") || label.equalsIgnoreCase("wp")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "Wild Pets v" + WildPets.getInstance().getVersion());
                sender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Wild-Pets/wiki");
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = getArguments(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                checkPermission(sender, "wp.help");
                HelpCommand command = new HelpCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("tame")) {
                checkPermission(sender, "wp.tame");
                TameCommand command = new TameCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("select")) {
                checkPermission(sender, "wp.select");
                SelectCommand command = new SelectCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("rename")) {
                checkPermission(sender, "wp.rename");
                RenameCommand command = new RenameCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("info")) {
                checkPermission(sender, "wp.info");
                InfoCommand command = new InfoCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("stay")) {
                checkPermission(sender, "wp.stay");
                StayCommand command = new StayCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("wander")) {
                checkPermission(sender, "wp.wander");
                WanderCommand command = new WanderCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("list")) {
                checkPermission(sender, "wp.list");
                ListCommand command = new ListCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("call")) {
                checkPermission(sender, "wp.call");
                CallCommand command = new CallCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("setfree")) {
                checkPermission(sender, "wp.setfree");
                SetFreeCommand command = new SetFreeCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("locate")) {
                checkPermission(sender, "wp.locate");
                LocateCommand command = new LocateCommand();
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

    private void checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "In order to use this command, you need the following permission: '" + permission + "'");
        }
    }

}
