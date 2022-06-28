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
    private final PersistentData persistentData;
    private final EphemeralData ephemeralData;

    public JoinAndQuitHandler(PersistentData persistentData, EphemeralData ephemeralData) {
        this.persistentData = persistentData;
        this.ephemeralData = ephemeralData;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (persistentData.getPetList(event.getPlayer().getUniqueId()) == null) {
            persistentData.createPetListForPlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        ephemeralData.clearPlayerFromLists(event.getPlayer());
    }
}