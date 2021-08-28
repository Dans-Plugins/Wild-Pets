package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadEventHandler implements Listener {

    @EventHandler()
    public void handle(ChunkLoadEvent event) {

        // loop through entities
        for (Entity entity : event.getChunk().getEntities()) {
            // get pet object associated with entity
            Pet pet = PersistentData.getInstance().getPet(entity);
            if (pet == null) {
                // not a pet, return without doing anything else
                return;
            }

            // get movement state
            String movementState = pet.getMovementState();

            // check if movement state is "Staying"
            if (movementState.equalsIgnoreCase("Staying")) {
                // schedule repeating teleport task if so
                pet.scheduleTeleportTask();
            }

            // set last known location since they just got rendered in again
            pet.setLastKnownLocation(entity.getLocation());
        }

    }

}
