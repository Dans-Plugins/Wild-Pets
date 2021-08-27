package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.PetList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!player.hasPermission("wp.list.others")) {
                sender.sendMessage(ChatColor.RED + "In order to view other players' pet lists, you need the following permission: 'wp.list.others'");
                return false;
            }
            String targetPlayerName = args[0];
            OfflinePlayer targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.RED + "That player wasn't found.");
                return false;
            }
            PetList petList = PersistentData.getInstance().getPetList(targetPlayer.getUniqueId());
            petList.sendListOfPetsToPlayer(player);
        }

        PersistentData.getInstance().sendListOfPetsToPlayer(player);
        return true;
    }

}
