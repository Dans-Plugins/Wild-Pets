package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class MoveHandler implements Listener {

    private boolean debug = WildPets.getInstance().isDebugEnabled();

    @EventHandler()
    public void handle(PlayerMoveEvent event) {
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
