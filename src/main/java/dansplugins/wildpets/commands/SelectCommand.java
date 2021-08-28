package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SelectCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("cancel")) {
                if (ConfigManager.getInstance().getBoolean("rightClickToSelect")) {
                    player.sendMessage(ChatColor.RED + "Usage: /wp select (petName)");
                    return false;
                }
                EphemeralData.getInstance().setPlayerAsNotSelecting(player);
                player.sendMessage(ChatColor.GREEN + "Selecting cancelled.");
                return true;
            }

            String petName = args[0];

            Pet pet = PersistentData.getInstance().getPlayersPet(player, petName);

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "You don't have any pets named " + petName + ".");
                return false;
            }

            EphemeralData.getInstance().selectPetForPlayer(pet, player);

            player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
            return true;
        }

        if (ConfigManager.getInstance().getBoolean("rightClickToSelect")) {
            player.sendMessage(ChatColor.RED + "Usage: /wp select (petName)");
            return false;
        }
        else {
            EphemeralData.getInstance().setPlayerAsSelecting(player);
            player.sendMessage(ChatColor.GREEN + "Right click on an entity to select it. Type '/wp select cancel' to cancel selecting.");
            return true;
        }

    }

}
