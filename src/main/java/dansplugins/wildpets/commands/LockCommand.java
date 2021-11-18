package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class LockCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("lock"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.lock"));

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
        return false;
    }

}