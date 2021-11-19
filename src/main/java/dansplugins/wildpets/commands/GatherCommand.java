package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class GatherCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("gather"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.gather"));

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
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }
        Player player = (Player) commandSender;

        PetList petList = PersistentData.getInstance().getPetList(player.getUniqueId());

        for (Pet pet : petList.getPets()) {
            Entity entity = Bukkit.getEntity(pet.getUniqueID());
            if (entity != null) {
                entity.teleport(player.getLocation());

            }
        }
        player.sendMessage(ChatColor.GREEN + "Your pets gather near you.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}