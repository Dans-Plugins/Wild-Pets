package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TameCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("cancel")) {
            EphemeralData.getInstance().setPlayerAsNotTaming(player);
            player.sendMessage(ChatColor.GREEN + "Taming cancelled.");
            return true;
        }

        EphemeralData.getInstance().setPlayerAsTaming(player);
        player.sendMessage(ChatColor.GREEN + "Right click on an entity to tame it. Type '/wp tame cancel' to cancel taming.");
        return true;
    }

}
