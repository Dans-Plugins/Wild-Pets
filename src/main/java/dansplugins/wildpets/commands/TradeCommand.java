package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 * Command to transfer a selected pet to another online player.
 */
public class TradeCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;
    private final PetListRepository petListRepository;
    private final PetRecordRepository petRecordRepository;

    public TradeCommand(EphemeralData ephemeralData, PetListRepository petListRepository, PetRecordRepository petRecordRepository) {
        super(new ArrayList<>(Arrays.asList("trade")), new ArrayList<>(Arrays.asList("wp.trade")));
        this.ephemeralData = ephemeralData;
        this.petListRepository = petListRepository;
        this.petRecordRepository = petRecordRepository;
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Usage: /wp trade <playerName>");
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        Pet pet = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());
        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected. Use /wp select first.");
            return false;
        }

        if (args.length < 1) {
            return execute(sender);
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return false;
        }

        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot trade a pet to yourself.");
            return false;
        }

        String petName = pet.getName();

        boolean success = petListRepository.transferPet(pet, targetPlayer.getUniqueId());
        if (!success) {
            player.sendMessage(ChatColor.RED + "Failed to transfer pet.");
            return false;
        }

        // Update pet record
        try {
            PetRecord petRecord = petRecordRepository.getPetRecord(pet.getUniqueID());
            petRecord.setOwnerUUID(targetPlayer.getUniqueId());
        } catch (PetRecordNotFoundException e) {
            petRecordRepository.addPetRecord(pet);
        }

        ephemeralData.clearPetSelectionForPlayer(player.getUniqueId());

        player.sendMessage(ChatColor.GREEN + petName + " has been traded to " + targetPlayer.getName() + ".");
        targetPlayer.sendMessage(ChatColor.GREEN + player.getName() + " has traded " + petName + " to you.");
        return true;
    }
}
