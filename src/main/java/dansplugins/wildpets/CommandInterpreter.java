package dansplugins.wildpets;

import dansplugins.wildpets.commands.TameCommand;
import org.bukkit.command.CommandSender;

public class CommandInterpreter {

    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("wildpets") || label.equalsIgnoreCase("wp")) {

            if (args.length == 0) {
                return false;
            }

            if (args[0].equalsIgnoreCase("tame")) {
                TameCommand command = new TameCommand();
                return command.execute(sender);
            }

        }

        return false;
    }

}
