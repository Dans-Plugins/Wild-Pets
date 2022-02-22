package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class RevokeAccessCommand extends AbstractPluginCommand {

    public RevokeAccessCommand() {
        super(new ArrayList<>(Arrays.asList("revokeaccess")), new ArrayList<>(Arrays.asList("wp.revokeaccess")));
    }

    public boolean execute(CommandSender sender) {
        // TODO: implement
        sender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return true;
    }
}