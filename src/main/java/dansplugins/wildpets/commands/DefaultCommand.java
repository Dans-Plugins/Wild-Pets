package dansplugins.wildpets.commands;

import dansplugins.wildpets.WildPets;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

/**
 * @author Daniel Stephenson
 */
public class DefaultCommand extends AbstractCommand {

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "Wild Pets " + WildPets.getInstance().getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Wild-Pets/wiki");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}