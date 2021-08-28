package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadEventHandler implements Listener {

    @EventHandler()
    public void handle(ChunkLoadEvent event) {
        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Handling ChunkLoadEvent!"); }

        System.out.println("Number of entities: " + event.getChunk().getEntities().length);

        // loop through entities
        for (int i = 0; i < event.getChunk().getEntities().length; i++) {
            Entity entity = event.getChunk().getEntities()[i];
            if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Looping through entity " + entity.getType().name()); }
            // get pet object associated with entity
            Pet pet = PersistentData.getInstance().getPet(entity);
            if (pet == null) {
                if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Not a pet!"); }
                // not a pet, return without doing anything else
                return;
            }
            if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Pet detected!"); }

            // get movement state
            String movementState = pet.getMovementState();

            // check if movement state is "Staying"
            if (movementState.equalsIgnoreCase("Staying")) {
                if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Pet is staying!"); }
                // schedule repeating teleport task if so
                pet.scheduleTeleportTask();
            }

            // set last known location since they just got rendered in again
            pet.setLastKnownLocation(entity.getLocation());
        }

    }

}
