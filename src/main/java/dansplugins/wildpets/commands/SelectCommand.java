package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class SelectCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("select"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.select"));

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

            if (args[0].equalsIgnoreCase("cancel")) {
                if (ConfigManager.getInstance().getBoolean("rightClickToSelect")) {
                    player.sendMessage(ChatColor.RED + "Usage: /wp select (petName)");
                    return false;
                }
                EphemeralData.getInstance().setPlayerAsNotSelecting(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Selecting cancelled.");
                return true;
            }

            String petName = args[0];

            Pet pet = PersistentData.getInstance().getPlayersPet(player, petName);

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "You don't have any pets named " + petName + ".");
                return false;
            }

            EphemeralData.getInstance().selectPetForPlayer(pet, player.getUniqueId());

            player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
            return true;
        }

        if (ConfigManager.getInstance().getBoolean("rightClickToSelect")) {
            player.sendMessage(ChatColor.RED + "Usage: /wp select (petName)");
            return false;
        }
        else {
            EphemeralData.getInstance().setPlayerAsSelecting(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Right click on an entity to select it. Type '/wp select cancel' to cancel selecting.");
            return true;
        }

    }

}
