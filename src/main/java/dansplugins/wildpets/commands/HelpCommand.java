package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
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
        player.sendMessage(ChatColor.AQUA + "/wp select - Select a pet by interaction.");
        player.sendMessage(ChatColor.AQUA + "/wp select (petName) - Select a pet by name.");
        player.sendMessage(ChatColor.AQUA + "/wp rename (newName) - Rename selected pet.");
        player.sendMessage(ChatColor.AQUA + "/wp stay - Make selected pet stay.");
        player.sendMessage(ChatColor.AQUA + "/wp wander - make selected pet wander.");
        return true;
    }

}
