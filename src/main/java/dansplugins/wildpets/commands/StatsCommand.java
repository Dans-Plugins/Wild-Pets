package dansplugins.wildpets.commands;

import dansplugins.wildpets.pet.list.PetListRepository;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {
    private final PetListRepository petListRepository;

    // TODO: add statistics object with persistent data like number of times players have attempted to tame

    public StatsCommand(PetListRepository petListRepository) {
        super(new ArrayList<>(Arrays.asList("stats")), new ArrayList<>(Arrays.asList("wp.stats")));
        this.petListRepository = petListRepository;
    }

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Wild Pets Statistics ===");
        sender.sendMessage(ChatColor.AQUA + "Number of Pets: " + petListRepository.getAllPets().size());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}