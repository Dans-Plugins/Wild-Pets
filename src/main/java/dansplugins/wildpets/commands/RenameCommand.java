package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RenameCommand {

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /wp rename (new name)");
            return false;
        }

        String newName = args[0];

        Pet pet = EphemeralData.getInstance().getPetSelectionForPlayer(player);

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        if (PersistentData.getInstance().getPetList(player.getUniqueId()).isNameTaken(newName)) {
            player.sendMessage(ChatColor.RED + "That name is already taken by one of your pets.");
            return false;
        }

        if (newName.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.RED + "You can't name your pet 'cancel'.");
            return false;
        }

        pet.setName(newName);
        player.sendMessage(ChatColor.GREEN + "Renamed.");
        return true;
    }

}
