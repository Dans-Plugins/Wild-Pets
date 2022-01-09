package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Daniel McCoy Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {

    // TODO: add statistics object with persistent data like number of times players have attempted to tame

    public StatsCommand() {
        super(new ArrayList<>(Arrays.asList("stats")), new ArrayList<>(Arrays.asList("wp.stats")));
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Wild Pets Statistics ===");
        sender.sendMessage(ChatColor.AQUA + "Number of Pets: " + PersistentData.getInstance().getAllPets().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}