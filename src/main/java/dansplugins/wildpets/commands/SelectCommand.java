package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
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
            String petName = args[0];

            Pet pet = PersistentData.getInstance().getPlayersPet(player, petName);

            EphemeralData.getInstance().selectPetForPlayer(pet, player);

            player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
            return true;
        }

        EphemeralData.getInstance().setPlayerAsSelecting(player);
        player.sendMessage(ChatColor.GREEN + "Right click on an entity to select it.");
        return true;
    }

}
