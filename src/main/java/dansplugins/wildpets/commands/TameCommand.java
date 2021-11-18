package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class TameCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("tame"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.tame"));

    @Override
    public ArrayList<String> getNames() {
        return names;
    }

    @Override
    public ArrayList<String> getPermissions() {
        return permissions;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("cancel")) {
            EphemeralData.getInstance().setPlayerAsNotTaming(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Taming cancelled.");
            return true;
        }

        EphemeralData.getInstance().setPlayerAsTaming(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Right click on an entity to tame it. Type '/wp tame cancel' to cancel taming.");
        return true;
    }

}
