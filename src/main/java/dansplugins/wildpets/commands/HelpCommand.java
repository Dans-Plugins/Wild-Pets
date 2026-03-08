package dansplugins.wildpets.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.utils.MessageFormat;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {
    private final ConfigService configService;

    public HelpCommand(ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("wp.help")));
        this.configService = configService;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        player.sendMessage("");
        player.sendMessage(MessageFormat.header("Wild Pets", "Commands"));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp help " + ChatColor.GRAY + "- View a list of helpful commands."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp tame " + ChatColor.GRAY + "- Tame an entity."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp list " + ChatColor.GRAY + "- List tamed pets."));
        if (!configService.getBoolean("rightClickToSelect")) {
            player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp select " + ChatColor.GRAY + "- Select a pet by interaction."));
        }
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp select (petName) " + ChatColor.GRAY + "- Select a pet by name."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp info " + ChatColor.GRAY + "- View selected pet info."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp rename (newName) " + ChatColor.GRAY + "- Rename selected pet."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp wander " + ChatColor.GRAY + "- Make selected pet wander."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp follow " + ChatColor.GRAY + "- Make selected pet follow you."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp stay " + ChatColor.GRAY + "- Make selected pet stay in place."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp call " + ChatColor.GRAY + "- Call selected pet to come to you."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp locate " + ChatColor.GRAY + "- Locate your selected pet."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp lock " + ChatColor.GRAY + "- Lock your pet."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp unlock " + ChatColor.GRAY + "- Unlock your pet."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp setfree " + ChatColor.GRAY + "- Set your pet free."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp gather " + ChatColor.GRAY + "- Gather your pets in one place."));
        player.sendMessage(MessageFormat.line(ChatColor.AQUA + "/wp config " + ChatColor.GRAY + "- View or set config options."));
        player.sendMessage(MessageFormat.footer());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
