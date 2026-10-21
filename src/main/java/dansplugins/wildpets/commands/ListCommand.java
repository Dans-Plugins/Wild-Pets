package dansplugins.wildpets.commands;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.list.PetList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class ListCommand extends AbstractPluginCommand {
    private final PetListRepository petListRepository;
    private final ServerProvider serverProvider;

    public ListCommand(PetListRepository petListRepository) {
        this(petListRepository, new ServerProvider());
    }

    public ListCommand(PetListRepository petListRepository, ServerProvider serverProvider) {
        super(new ArrayList<>(Arrays.asList("list")), new ArrayList<>(Arrays.asList("wp.list")));
        this.petListRepository = petListRepository;
        this.serverProvider = serverProvider;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) commandSender;
        petListRepository.sendListOfPetsToPlayer(player);
        return true;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("wp.list.others")) {
            sender.sendMessage(ChatColor.RED + "In order to view other players' pet lists, you need the following permission: 'wp.list.others'");
            return false;
        }
        String targetPlayerName = args[0];
        OfflinePlayer targetPlayer = serverProvider.get().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }
        PetList petList = petListRepository.getPetList(targetPlayer.getUniqueId());
        petList.sendListOfPetsToPlayer(player);
        return true;
    }
}
