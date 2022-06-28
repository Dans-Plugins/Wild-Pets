package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.PetList;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class ListCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;

    public ListCommand(PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("list")), new ArrayList<>(Arrays.asList("wp.list")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;
        persistentData.sendListOfPetsToPlayer(player);
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
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
        PetList petList = persistentData.getPetList(targetPlayer.getUniqueId());
        petList.sendListOfPetsToPlayer(player);
        return true;
    }
}