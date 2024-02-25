package dansplugins.wildpets.listeners;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class JoinAndQuitHandler implements Listener {
    private final PetListRepository petListRepository;
    private final EphemeralData ephemeralData;

    public JoinAndQuitHandler(PetListRepository petListRepository, EphemeralData ephemeralData) {
        this.petListRepository = petListRepository;
        this.ephemeralData = ephemeralData;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        if (petListRepository.getPetList(event.getPlayer().getUniqueId()) == null) {
            petListRepository.createPetListForPlayer(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        ephemeralData.clearPlayerFromLists(event.getPlayer());
    }
}