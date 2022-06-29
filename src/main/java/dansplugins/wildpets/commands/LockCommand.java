package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class LockCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;

    public LockCommand(EphemeralData ephemeralData) {
        super(new ArrayList<>(Arrays.asList("lock")), new ArrayList<>(Arrays.asList("wp.lock")));
        this.ephemeralData = ephemeralData;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        ephemeralData.setPlayerAsLocking(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Right click one of your pets to lock it.");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}