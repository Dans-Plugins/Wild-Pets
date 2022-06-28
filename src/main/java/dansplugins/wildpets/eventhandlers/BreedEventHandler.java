package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;

import dansplugins.wildpets.services.ConfigService;
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
    private final PersistentData persistentData;
    private final ConfigService configService;
    private final EphemeralData ephemeralData;

    public BreedEventHandler(PersistentData persistentData, ConfigService configService, EphemeralData ephemeralData) {
        this.persistentData = persistentData;
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

        Pet petParent1 = persistentData.getPet(parent1);
        Pet petParent2 = persistentData.getPet(parent2);

        if (petParent1 != null) {
            petParent1.addChild(child.getUniqueId());
        }

        if (petParent2 != null) {
            petParent2.addChild(child.getUniqueId());
        }

        if (configService.getBoolean("bornPetsEnabled")) {
            if (petParent1 != null || petParent2 != null) {
                persistentData.addNewPet(player, child);
                Pet newPet = persistentData.getPet(child);
                ephemeralData.selectPetForPlayer(newPet, player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "You have a new pet named " + newPet.getName() + " and it is now your selected pet.");

                newPet.addParent(parent1.getUniqueId());
                newPet.addParent(parent2.getUniqueId());
            }
        }
    }
}