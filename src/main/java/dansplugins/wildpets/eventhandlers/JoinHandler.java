package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.PersistentData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (!PersistentData.getInstance().hasPetList(event.getPlayer())) {

            PersistentData.getInstance().createPetListForPlayer(event.getPlayer());

        }
    }

}
