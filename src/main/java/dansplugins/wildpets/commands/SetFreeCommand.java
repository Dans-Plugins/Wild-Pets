package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
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
public class SetFreeCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;
    private final PersistentData persistentData;

    public SetFreeCommand(EphemeralData ephemeralData, PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("setfree")), new ArrayList<>(Arrays.asList("wp.setfree")));
        this.ephemeralData = ephemeralData;
        this.persistentData = persistentData;
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

        String petName = pet.getName();

        persistentData.removePet(pet);
        player.sendMessage(ChatColor.GREEN + petName + " has been set free.");
        ephemeralData.clearPetSelectionForPlayer(player.getUniqueId());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}