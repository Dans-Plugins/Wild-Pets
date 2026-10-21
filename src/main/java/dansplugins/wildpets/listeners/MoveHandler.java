package dansplugins.wildpets.listeners;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetList;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

/**
 * @author Daniel McCoy Stephenson
 */
public class MoveHandler implements Listener {
    private final PetListRepository petListRepository;
    private final ServerProvider serverProvider;

    public MoveHandler(PetListRepository petListRepository) {
        this(petListRepository, new ServerProvider());
    }

    public MoveHandler(PetListRepository petListRepository, ServerProvider serverProvider) {
        this.petListRepository = petListRepository;
        this.serverProvider = serverProvider;
    }

    @EventHandler()
    public void handle(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();
        if (fromChunk.getX() == toChunk.getX() && fromChunk.getZ() == toChunk.getZ()) {
            // return if same chunk
            return;
        }
        Player player = event.getPlayer();
        PetList petList = petListRepository.getPetList(player.getUniqueId());
        ArrayList<Pet> followingPets = petList.getFollowingPets();
        if (followingPets.size() != 0) {
            for (Pet pet : followingPets) {
                Entity entity = serverProvider.get().getEntity(pet.getUniqueID());
                if (entity != null) {
                    entity.teleport(player);
                }
            }
        }
    }
}