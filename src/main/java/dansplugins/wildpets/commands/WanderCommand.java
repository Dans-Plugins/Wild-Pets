package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class WanderCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;

    public WanderCommand(EphemeralData ephemeralData) {
        super(new ArrayList<>(Arrays.asList("wander")), new ArrayList<>(Arrays.asList("wp.wander")));
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

        pet.setWandering();
        player.sendMessage(ChatColor.GREEN + pet.getName() + " is now wandering.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}