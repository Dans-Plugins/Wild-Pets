package dansplugins.wildpets.listeners;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class BreedEventHandler implements Listener {
    private final PetListRepository petListRepository;
    private final PetRecordRepository petRecordRepository;
    private final ConfigService configService;
    private final EphemeralData ephemeralData;

    public BreedEventHandler(PetListRepository petListRepository, PetRecordRepository petRecordRepository, ConfigService configService, EphemeralData ephemeralData) {
        this.petListRepository = petListRepository;
        this.petRecordRepository = petRecordRepository;
        this.configService = configService;
        this.ephemeralData = ephemeralData;
    }

    @EventHandler()
    public void handle(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getBreeder();

        Entity child = event.getEntity();

        Entity parent1 = event.getFather();
        Entity parent2 = event.getMother();

        Pet petParent1 = petListRepository.getPet(parent1);
        Pet petParent2 = petListRepository.getPet(parent2);

        if (petParent1 != null) {
            petParent1.addChild(child.getUniqueId());
        }

        if (petParent2 != null) {
            petParent2.addChild(child.getUniqueId());
        }

        if (configService.getBoolean("bornPetsEnabled")) {
            if (petParent1 != null || petParent2 != null) {
                petListRepository.addNewPet(player, child);
                Pet newPet = petListRepository.getPet(child);
                petRecordRepository.addPetRecord(newPet);
                ephemeralData.selectPetForPlayer(newPet, player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "You have a new pet named " + newPet.getName() + " and it is now your selected pet.");

                newPet.addParent(parent1.getUniqueId());
                newPet.addParent(parent2.getUniqueId());
            }
        }
    }
}