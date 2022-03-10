package dansplugins.wildpets.commands;

import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.wildpets.services.LocalConfigService;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("wp.help")));
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        player.sendMessage(ChatColor.AQUA + "=== List of Commands ===");
        player.sendMessage(ChatColor.AQUA + "/wp help - View a list of helpful commands.");
        player.sendMessage(ChatColor.AQUA + "/wp tame - Tame an entity.");
        player.sendMessage(ChatColor.AQUA + "/wp list - List tamed pets.");
        if (!LocalConfigService.getInstance().getBoolean("rightClickToSelect")) {
            player.sendMessage(ChatColor.AQUA + "/wp select - Select a pet by interaction.");
        }
        player.sendMessage(ChatColor.AQUA + "/wp select (petName) - Select a pet by name.");
        player.sendMessage(ChatColor.AQUA + "/wp info - View selected pet info.");
        player.sendMessage(ChatColor.AQUA + "/wp rename (newName) - Rename selected pet.");
        player.sendMessage(ChatColor.AQUA + "/wp wander - Make selected pet wander.");
        player.sendMessage(ChatColor.AQUA + "/wp follow - Make selected pet follow you.");
        player.sendMessage(ChatColor.AQUA + "/wp call - Call selected pet to come to you.");
        player.sendMessage(ChatColor.AQUA + "/wp locate - Locate your selected pet.");
        player.sendMessage(ChatColor.AQUA + "/wp lock - Lock your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp unlock - Unock your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp checkaccess - Check who has access to your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp grantaccess - Grant someone access to your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp revokeaccess - Revoke access for someone to your pet.");
        player.sendMessage(ChatColor.AQUA + "/wp gather - Gather your pets in one place.");
        player.sendMessage(ChatColor.AQUA + "/wp config - View or set config options.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}