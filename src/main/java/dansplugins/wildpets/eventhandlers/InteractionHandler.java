package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.objects.Pet;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class InteractionHandler implements Listener {

    @EventHandler()
    public void handle(PlayerInteractAtEntityEvent event) {

        Entity clickedEntity = event.getRightClicked();

        Pet newPet = new Pet(clickedEntity, event.getPlayer());

    }

}
