package dansplugins.wildpets.commands;

import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.utils.MessageFormat;

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
        int numPets = petListRepository.getAllPets().size();
        sender.sendMessage("");
        sender.sendMessage(MessageFormat.header("Wild Pets", "Statistics"));
        sender.sendMessage(MessageFormat.line(ChatColor.GRAY + "Number of Pets: " +
                ChatColor.GREEN + numPets));
        sender.sendMessage(MessageFormat.footer());
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
