package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
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
public class GatherCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;

    public GatherCommand(PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("gather")), new ArrayList<>(Arrays.asList("wp.gather")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;

        PetList petList = persistentData.getPetList(player.getUniqueId());

        if (petList.getNumPets() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any pets.");
            return false;
        }

        for (Pet pet : petList.getPets()) {
            Entity entity = Bukkit.getEntity(pet.getUniqueID());
            if (entity != null) {
                entity.teleport(player.getLocation());

            }
        }
        player.sendMessage(ChatColor.GREEN + "Your pets gather nearby.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}