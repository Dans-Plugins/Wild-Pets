package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class CallCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("call"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.call"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean execute(CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        Pet pet = EphemeralData.getInstance().getPetSelectionForPlayer(player.getUniqueId());

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        Entity entity = Bukkit.getEntity(pet.getUniqueID());

        if (entity != null) {
            entity.teleport(player.getLocation());
            player.sendMessage(ChatColor.GREEN + pet.getName() + " has answered your call.");
            return true;
        }
        else {
            player.sendMessage(ChatColor.RED + pet.getName() + " cannot be found.");
            return false;
        }
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}
