package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class LocateCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("locate"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.locate"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
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

        pet.sendLocationToPlayer(player);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }

}
