package dansplugins.wildpets.listeners;

import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * Handles chunk load events to ensure pet entities retain their
 * persistence flags, preventing them from despawning.
 *
 * @author Daniel McCoy Stephenson
 */
public class ChunkLoadHandler implements Listener {
    private final PetListRepository petListRepository;

    public ChunkLoadHandler(PetListRepository petListRepository) {
        this.petListRepository = petListRepository;
    }

    @EventHandler()
    public void handle(ChunkLoadEvent event) {
        if (petListRepository.hasNoTrackedPets()) {
            // Avoid scanning the chunk's entities when no pets are tracked at all
            return;
        }
        for (Entity entity : event.getChunk().getEntities()) {
            Pet pet = petListRepository.getPet(entity);
            if (pet != null) {
                pet.ensurePersistence(entity);
            }
        }
    }
}
