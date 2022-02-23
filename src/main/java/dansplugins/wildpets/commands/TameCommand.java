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
public class TameCommand extends AbstractPluginCommand {

    public TameCommand() {
        super(new ArrayList<>(Arrays.asList("tame")), new ArrayList<>(Arrays.asList("wp.tame")));
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        EphemeralData.getInstance().setPlayerAsTaming(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Right click on an entity to tame it. Type '/wp tame cancel' to cancel taming.");
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("cancel")) {
            EphemeralData.getInstance().setPlayerAsNotTaming(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Taming cancelled.");
            return true;
        }

        return execute(sender);
    }
}