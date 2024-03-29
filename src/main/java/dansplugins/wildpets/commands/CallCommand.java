package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.pet.Pet;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class CallCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;

    public CallCommand(EphemeralData ephemeralData) {
        super(new ArrayList<>(Arrays.asList("call")), new ArrayList<>(Arrays.asList("wp.call")));
        this.ephemeralData = ephemeralData;
    }

    public boolean execute(CommandSender sender) {

        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        Pet pet = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());

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