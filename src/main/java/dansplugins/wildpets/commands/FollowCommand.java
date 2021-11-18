package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class FollowCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("follow"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.follow"));

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

        pet.setFollowing();
        player.sendMessage(ChatColor.GREEN + pet.getName() + " is now following you.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return false;
    }

}
