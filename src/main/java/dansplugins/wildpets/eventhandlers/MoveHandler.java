package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class MoveHandler implements Listener {

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
        PetList petList = PersistentData.getInstance().getPetList(player.getUniqueId());
        ArrayList<Pet> followingPets = petList.getFollowingPets();
        if (followingPets.size() != 0) {
            for (Pet pet : followingPets) {
                Entity entity = Bukkit.getServer().getEntity(pet.getUniqueID());
                if (entity != null) {
                    entity.teleport(player);
                }
            }
        }
    }

}
