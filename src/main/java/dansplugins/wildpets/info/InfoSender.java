package dansplugins.wildpets.info;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import java.util.UUID;

public class InfoSender {
    private final ConfigService configService;
    private final PetRecordRepository petRecordRepository;

    public InfoSender(ConfigService configService, PetRecordRepository petRecordRepository) {
        this.configService = configService;
        this.petRecordRepository = petRecordRepository;
    }

    public void sendInfoToPlayer(Player player, Pet pet) {
        UUIDChecker uuidChecker = new UUIDChecker();
        player.sendMessage(ChatColor.AQUA + "=== Pet Info ===");
        player.sendMessage(ChatColor.AQUA + "Name: " + pet.getName());
        player.sendMessage(ChatColor.AQUA + "Owner: " + uuidChecker.findPlayerNameBasedOnUUID(pet.getOwnerUUID()));
        player.sendMessage(ChatColor.AQUA + "State: " + pet.getMovementState());
        player.sendMessage(ChatColor.AQUA + "Locked: " + pet.isLocked());
        if (configService.getBoolean("showLineageInfo")) {
            if (pet.getParentUUIDs().size() > 0) {
                player.sendMessage(ChatColor.AQUA + "Parents: " + getParentNamesSeparatedByCommas(pet));
            }
            if (pet.getChildUUIDs().size() > 0) {
                player.sendMessage(ChatColor.AQUA + "Children: " + getChildrenNamesSeparatedByCommas(pet));
            }
        }
        if (configService.getBoolean("debugMode")) {
            player.sendMessage(ChatColor.AQUA + "[DEBUG] uniqueID: " + pet.getUniqueID().toString());
            player.sendMessage(ChatColor.AQUA + "[DEBUG] ownerUUID: " + pet.getOwnerUUID().toString());
            player.sendMessage(ChatColor.AQUA + "[DEBUG] assignedID: " + pet.getAssignedID());
            if (pet.getParentUUIDs().size() > 0) {
                player.sendMessage(ChatColor.AQUA + "[DEBUG] Parents: " + pet.getParentsUUIDsSeparatedByCommas());
            }
            if (pet.getChildUUIDs().size() > 0) {
                player.sendMessage(ChatColor.AQUA + "[DEBUG] Children: " + pet.getChildrenUUIDsSeparatedByCommas());
            }
        }
    }

    public String getParentNamesSeparatedByCommas(Pet pet) {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : pet.getParentUUIDs()) {
            PetRecord petRecord = petRecordRepository.getPetRecord(uuid);
            if (petRecord == null) {
                continue;
            }
            toReturn = toReturn + petRecord.getName();
            count++;
            if (count != pet.getParentUUIDs().size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }

    public String getChildrenNamesSeparatedByCommas(Pet pet) {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : pet.getChildUUIDs()) {
            PetRecord petRecord = petRecordRepository.getPetRecord(uuid);
            if (petRecord == null) {
                continue;
            }
            toReturn = toReturn + petRecord.getName();
            count++;
            if (count != pet.getChildUUIDs().size()) {
                toReturn = toReturn + ", ";
            }
        }
        return toReturn;
    }
}
