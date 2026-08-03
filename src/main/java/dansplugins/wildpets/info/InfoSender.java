package dansplugins.wildpets.info;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import dansplugins.wildpets.utils.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import dansplugins.wildpets.helpers.UUIDChecker;

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
        player.sendMessage("");
        player.sendMessage(MessageFormat.header("Wild Pets", "Pet Info"));
        player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Name:     " + ChatColor.WHITE + pet.getName()));
        player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Owner:    " + ChatColor.WHITE + uuidChecker.findPlayerNameBasedOnUUID(pet.getOwnerUUID())));
        player.sendMessage(MessageFormat.line(ChatColor.GRAY + "State:    " + ChatColor.WHITE + pet.getMovementState()));
        player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Locked:   " + (pet.isLocked() ? ChatColor.RED + "true" : ChatColor.GREEN + "false")));
        if (configService.getBoolean("showLineageInfo")) {
            if (pet.getParentUUIDs().size() > 0) {
                player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Parents:  " + ChatColor.WHITE + getParentNamesSeparatedByCommas(pet)));
            }
            if (pet.getChildUUIDs().size() > 0) {
                player.sendMessage(MessageFormat.line(ChatColor.GRAY + "Children: " + ChatColor.WHITE + getChildrenNamesSeparatedByCommas(pet)));
            }
        }
        if (configService.getBoolean("debugMode")) {
            player.sendMessage(MessageFormat.line(ChatColor.GRAY + "[DEBUG] uniqueID:   " + ChatColor.WHITE + pet.getUniqueID().toString()));
            player.sendMessage(MessageFormat.line(ChatColor.GRAY + "[DEBUG] ownerUUID:  " + ChatColor.WHITE + pet.getOwnerUUID().toString()));
            player.sendMessage(MessageFormat.line(ChatColor.GRAY + "[DEBUG] assignedID: " + ChatColor.WHITE + pet.getAssignedID()));
            if (pet.getParentUUIDs().size() > 0) {
                player.sendMessage(MessageFormat.line(ChatColor.GRAY + "[DEBUG] Parents:    " + ChatColor.WHITE + pet.getParentsUUIDsSeparatedByCommas()));
            }
            if (pet.getChildUUIDs().size() > 0) {
                player.sendMessage(MessageFormat.line(ChatColor.GRAY + "[DEBUG] Children:   " + ChatColor.WHITE + pet.getChildrenUUIDsSeparatedByCommas()));
            }
        }
        player.sendMessage(MessageFormat.footer());
    }

    public String getParentNamesSeparatedByCommas(Pet pet) {
        String toReturn = "";
        int count = 0;
        for (UUID uuid : pet.getParentUUIDs()) {
            PetRecord petRecord;
            try {
                petRecord = petRecordRepository.getPetRecord(uuid);
            } catch (PetRecordNotFoundException e) {
                toReturn = toReturn + "Unknown";
                count++;
                if (count != pet.getParentUUIDs().size()) {
                    toReturn = toReturn + ", ";
                }
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
            PetRecord petRecord;
            try {
                petRecord = petRecordRepository.getPetRecord(uuid);
            } catch (PetRecordNotFoundException e) {
                toReturn = toReturn + "Unknown";
                count++;
                if (count != pet.getChildUUIDs().size()) {
                    toReturn = toReturn + ", ";
                }
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
