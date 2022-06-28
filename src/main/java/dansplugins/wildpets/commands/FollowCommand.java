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
public class FollowCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;

    public FollowCommand(EphemeralData ephemeralData) {
        super(new ArrayList<>(Arrays.asList("follow")), new ArrayList<>(Arrays.asList("wp.follow")));
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

        pet.setFollowing();
        player.sendMessage(ChatColor.GREEN + pet.getName() + " is now following you.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}