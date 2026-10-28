package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.info.InfoSender;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecordRepository;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {
    private final EphemeralData ephemeralData;
    private final ConfigService configService;
    private final PetRecordRepository petRecordRepository;
    private final ServerProvider serverProvider;

    public InfoCommand(EphemeralData ephemeralData, ConfigService configService, PetRecordRepository petRecordRepository) {
        this(ephemeralData, configService, petRecordRepository, new ServerProvider());
    }

    public InfoCommand(EphemeralData ephemeralData, ConfigService configService, PetRecordRepository petRecordRepository, ServerProvider serverProvider) {
        super(new ArrayList<>(Arrays.asList("info")), new ArrayList<>(Arrays.asList("wp.info")));
        this.ephemeralData = ephemeralData;
        this.configService = configService;
        this.petRecordRepository = petRecordRepository;
        this.serverProvider = serverProvider;
    }

    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return false;
        }

        Player player = (Player) sender;

        Pet pet = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());

        if (pet == null) {
            player.sendMessage(ChatColor.RED + "No pet selected.");
            return false;
        }

        InfoSender infoSender = new InfoSender(configService, petRecordRepository, serverProvider);
        infoSender.sendInfoToPlayer(player, pet);
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}
