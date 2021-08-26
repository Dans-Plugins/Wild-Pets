package dansplugins.wildpets;

import dansplugins.wildpets.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("wildpets") || label.equalsIgnoreCase("wp")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "Wild Pets " + WildPets.getInstance().getVersion());
                sender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Wild-Pets/wiki");
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = getArguments(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                if (!checkPermission(sender, "wp.help")) { return false; }
                HelpCommand command = new HelpCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("tame")) {
                if (!checkPermission(sender, "wp.tame")) { return false; }
                TameCommand command = new TameCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("select")) {
                if (!checkPermission(sender, "wp.select")) { return false; }
                SelectCommand command = new SelectCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("rename")) {
                if (!checkPermission(sender, "wp.rename")) { return false; }
                RenameCommand command = new RenameCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("info")) {
                if (!checkPermission(sender, "wp.info")) { return false; }
                InfoCommand command = new InfoCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("stay")) {
                if (!checkPermission(sender, "wp.stay")) { return false; }
                StayCommand command = new StayCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("wander")) {
                if (!checkPermission(sender, "wp.wander")) { return false; }
                WanderCommand command = new WanderCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("follow")) {
                if (!checkPermission(sender, "wp.follow")) { return false; }
                FollowCommand command = new FollowCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("list")) {
                if (!checkPermission(sender, "wp.list")) { return false; }
                ListCommand command = new ListCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("call")) {
                if (!checkPermission(sender, "wp.call")) { return false; }
                CallCommand command = new CallCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("setfree")) {
                if (!checkPermission(sender, "wp.setfree")) { return false; }
                SetFreeCommand command = new SetFreeCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("locate")) {
                if (!checkPermission(sender, "wp.locate")) { return false; }
                LocateCommand command = new LocateCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("config")) {
                checkPermission(sender, "wp.config");
                ConfigCommand command = new ConfigCommand();
                return command.execute(sender, arguments);
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

    private boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "In order to use this command, you need the following permission: '" + permission + "'");
            return false;
        }
        return true;
    }

}
