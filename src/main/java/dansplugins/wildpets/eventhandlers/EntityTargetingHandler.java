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
        if (debug) { System.out.println("EntityTargetLivingEntityEvent is firing!"); }
        Entity targetingEntity = event.getEntity();
        Entity targetedEntity = event.getTarget();

        if (PersistentData.getInstance().getPet(targetingEntity) != null) {
            event.setTarget(null);
            if (targetingEntity instanceof Monster) {
                Monster monster = (Monster) targetingEntity;
                monster.setTarget(null);
            }
            if (debug) { System.out.println(event.getTarget().getName()); }
            if (debug) { System.out.println("Cancelling targeting event for a pet!"); }
            event.setCancelled(true);
        }
    }
}
