package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckAccessCommand {

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        EphemeralData.getInstance().setPlayerAsCheckingAccess(player);
        player.sendMessage(ChatColor.GREEN + "Right click a pet check who has access to it");
        return true;
    }

}