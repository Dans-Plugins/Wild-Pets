package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTargetingHandler implements Listener {

    private boolean debug = WildPets.getInstance().isDebugEnabled();

    @EventHandler()
    public void handle(EntityTargetLivingEntityEvent event) {
        Entity targetingEntity = event.getEntity();

        if (PersistentData.getInstance().getPet(targetingEntity) != null) {
            event.setTarget(null);
            if (targetingEntity instanceof Monster) {
                Monster monster = (Monster) targetingEntity;
                monster.setTarget(null);
                if (debug) { System.out.println("Cancelling targeting event for a pet!"); }
                event.setCancelled(true);
            }
        }
    }
}
