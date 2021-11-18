package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.misc.AbstractCommand;

import java.util.ArrayList;
import java.util.Collections;

public class GrantAccessCommand extends AbstractCommand {

    private ArrayList<String> names = new ArrayList<>(Collections.singletonList("grantaccess"));
    private ArrayList<String> permissions = new ArrayList<>(Collections.singletonList("wp.grantaccess"));

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
        // TODO: implement
        commandSender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        // TODO: implement
        sender.sendMessage(ChatColor.RED + "This command isn't implemented yet.");
        return true;
    }

}