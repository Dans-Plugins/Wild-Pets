package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Daniel McCoy Stephenson
 */
public class CheckAccessCommand extends AbstractPluginCommand {

    public CheckAccessCommand() {
        super(new ArrayList<>(Arrays.asList("checkaccess")), new ArrayList<>(Arrays.asList("wp.checkaccess")));
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        EphemeralData.getInstance().setPlayerAsCheckingAccess(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Right click a pet check who has access to it");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}