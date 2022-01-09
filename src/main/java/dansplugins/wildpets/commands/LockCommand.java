package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class LockCommand extends AbstractPluginCommand {

    public LockCommand() {
        super(new ArrayList<>(Arrays.asList("lock")), new ArrayList<>(Arrays.asList("wp.lock")));
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        EphemeralData.getInstance().setPlayerAsLocking(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Right click one of your pets to lock it.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}