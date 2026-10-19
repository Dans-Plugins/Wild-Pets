package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import dansplugins.wildpets.utils.MessageFormat;
import org.bukkit.entity.Entity;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class RenameCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;
    private final PetListRepository petListRepository;
    private final PetRecordRepository petRecordRepository;
    private final ConfigService configService;
    private final ServerProvider serverProvider;

    public RenameCommand(EphemeralData ephemeralData, PetListRepository petListRepository, PetRecordRepository petRecordRepository, ConfigService configService) {
        this(ephemeralData, petListRepository, petRecordRepository, configService, new ServerProvider());
    }

    public RenameCommand(EphemeralData ephemeralData, PetListRepository petListRepository, PetRecordRepository petRecordRepository, ConfigService configService, ServerProvider serverProvider) {
        super(new ArrayList<>(Arrays.asList("rename")), new ArrayList<>(Arrays.asList("wp.rename")));
        this.ephemeralData = ephemeralData;
        this.petListRepository = petListRepository;
        this.petRecordRepository = petRecordRepository;
        this.configService = configService;
        this.serverProvider = serverProvider;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.RED + "Usage: /wp rename (new name)");
        return false;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return execute(sender);
        }

        String newName = args[0];

        Pet pet = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        if (petListRepository.getPetList(player.getUniqueId()).isNameTaken(newName)) {
            player.sendMessage(ChatColor.RED + "That name is already taken by one of your pets.");
            return false;
        }

        if (newName.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.RED + "You can't name your pet 'cancel'.");
            return false;
        }

        int characterLimit = configService.getInt("petNameCharacterLimit");
        if (newName.length() > characterLimit) {
            player.sendMessage(ChatColor.RED + "Your pet's name can't contain more than " + characterLimit + " characters.");
            return false;
        }

        pet.setName(newName);
        Entity entity = serverProvider.get().getEntity(pet.getUniqueID());

        if (entity != null) {
            entity.setCustomName(ChatColor.GREEN + pet.getName());
        }
        PetRecord petRecord;
        try {
            petRecord = petRecordRepository.getPetRecord(pet.getUniqueID());
        } catch (PetRecordNotFoundException e) {
            petRecordRepository.addPetRecord(pet);
            try {
                petRecord = petRecordRepository.getPetRecord(pet.getUniqueID());
            } catch (PetRecordNotFoundException ex) {
                ex.printStackTrace();
                player.sendMessage(ChatColor.RED + "Pet record wasn't found & the plugin wasn't able to recover. Please report this problem to the community.");
                return false;
            }
        }
        petRecord.setName(newName);
        player.sendMessage("");
        player.sendMessage(MessageFormat.header("Wild Pets", "Rename"));
        player.sendMessage(MessageFormat.line(ChatColor.GREEN + "Renamed."));
        player.sendMessage(MessageFormat.footer());
        return true;
    }
}
