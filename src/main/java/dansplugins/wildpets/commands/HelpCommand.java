package dansplugins.wildpets.commands;

import dansplugins.wildpets.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.AQUA + "=== List of Commands ===");
        player.sendMessage(ChatColor.AQUA + "/wp help - View a list of helpful commands.");
        player.sendMessage(ChatColor.AQUA + "/wp tame - Tame an entity.");
        player.sendMessage(ChatColor.AQUA + "/wp list - List tamed pets.");
        if (!ConfigManager.getInstance().getBoolean("rightClickToSelect")) {
            player.sendMessage(ChatColor.AQUA + "/wp select - Select a pet by interaction.");
        }
        player.sendMessage(ChatColor.AQUA + "/wp select (petName) - Select a pet by name.");
        player.sendMessage(ChatColor.AQUA + "/wp info - View selected pet info.");
        player.sendMessage(ChatColor.AQUA + "/wp rename (newName) - Rename selected pet.");
        player.sendMessage(ChatColor.AQUA + "/wp stay - Make selected pet stay.");
        player.sendMessage(ChatColor.AQUA + "/wp wander - Make selected pet wander.");
        player.sendMessage(ChatColor.AQUA + "/wp follow - Make selected pet follow you.");
        player.sendMessage(ChatColor.AQUA + "/wp call - Call selected pet to come to you.");
        player.sendMessage(ChatColor.AQUA + "/wp locate - Locate your selected pet.");
        player.sendMessage(ChatColor.AQUA + "/wp lock - Lock your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp unlock - Unock your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp checkaccess - Check who has access to your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp config - View or set config options.");
        return true;
    }

}
