package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StatsCommand {

    // TODO: add statistics object with persistent data like number of times players have attempted to tame

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== Wild Pets Statistics ===");
        sender.sendMessage("Number of Pets: " + PersistentData.getInstance().getAllPets().size());
        return true;
    }

}
