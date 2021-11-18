package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.PetList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class ListCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("list"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.list"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        return false;
    }

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
            return true;
        }

        PersistentData.getInstance().sendListOfPetsToPlayer(player);
        return true;
    }

}
