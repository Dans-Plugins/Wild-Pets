package dansplugins.wildpets.commands;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.utils.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {
    private final WildPets wildPets;

    public DefaultCommand(WildPets wildPets) {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("wp.default")));
        this.wildPets = wildPets;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage("");
        commandSender.sendMessage(MessageFormat.header("Wild Pets", "Info"));
        commandSender.sendMessage(MessageFormat.line(ChatColor.GRAY + "Version:   " + ChatColor.WHITE + wildPets.getVersion()));
        commandSender.sendMessage(MessageFormat.line(ChatColor.GRAY + "Developer: " + ChatColor.WHITE + "DanTheTechMan"));
        commandSender.sendMessage(MessageFormat.line(ChatColor.GRAY + "Wiki:      " + ChatColor.WHITE + "https://github.com/dmccoystephenson/Wild-Pets/wiki"));
        commandSender.sendMessage(MessageFormat.line(ChatColor.GRAY + "Website:   " + ChatColor.WHITE + "https://dansplugins.com"));
        commandSender.sendMessage(MessageFormat.footer());
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
