package dansplugins.wildpets.commands;

import dansplugins.wildpets.WildPets;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

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
        commandSender.sendMessage(ChatColor.AQUA + "Wild Pets " + wildPets.getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Wild-Pets/wiki");
        commandSender.sendMessage(ChatColor.AQUA + "Website: https://dansplugins.com");
        return false;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}