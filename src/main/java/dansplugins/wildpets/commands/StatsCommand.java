package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class StatsCommand extends AbstractCommand {

    // TODO: add statistics object with persistent data like number of times players have attempted to tame

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("stats"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.stats"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Wild Pets Statistics ===");
        sender.sendMessage(ChatColor.AQUA + "Number of Pets: " + PersistentData.getInstance().getAllPets().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return false;
    }

}
