package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class GrantAccessCommand extends AbstractPluginCommand {

    public GrantAccessCommand() {
        super(new ArrayList<>(Arrays.asList("grantaccess")), new ArrayList<>(Arrays.asList("wp.grantaccess")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        // TODO: implement
        sender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return true;
    }
}