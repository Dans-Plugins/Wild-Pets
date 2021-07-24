package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CallCommand {

    public boolean execute(CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        Pet pet = EphemeralData.getInstance().getPetSelectionForPlayer(player);

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
            return false;
        }
    }

}
