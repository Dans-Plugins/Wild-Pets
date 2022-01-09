package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Daniel McCoy Stephenson
 */
public class CallCommand extends AbstractPluginCommand {

    public CallCommand() {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("wp.call")));
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