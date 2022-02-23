package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class JoinAndQuitHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (PersistentData.getInstance().getPetList(event.getPlayer().getUniqueId()) == null) {
            PersistentData.getInstance().createPetListForPlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        EphemeralData.getInstance().clearPlayerFromLists(event.getPlayer());
    }
}