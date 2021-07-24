package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InteractionHandler implements Listener {

    private boolean debug = true;

    @EventHandler()
    public void handle(PlayerInteractEntityEvent event) {
        if (debug) { System.out.println("Captured PlayerInteractEntity event!"); }

        Entity clickedEntity = event.getRightClicked();

        if (EphemeralData.getInstance().isPlayerTaming(event.getPlayer())) {

            if (PersistentData.getInstance().getPet(clickedEntity) != null) {
                event.getPlayer().sendMessage(ChatColor.RED + "That entity is already a pet.");
                EphemeralData.getInstance().setPlayerAsNotTaming(event.getPlayer());
                return;
            }

            PersistentData.getInstance().addNewPet(event.getPlayer(), clickedEntity);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Tamed.");
            EphemeralData.getInstance().setPlayerAsNotTaming(event.getPlayer());
        }
        else if (EphemeralData.getInstance().isPlayerSelecting(event.getPlayer())) {

            if (PersistentData.getInstance().getPet(clickedEntity) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "That entity is not a pet.");
                EphemeralData.getInstance().setPlayerAsNotSelecting(event.getPlayer());
                return;
            }

            if (PersistentData.getInstance().getPlayersPet(event.getPlayer(), clickedEntity) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "That entity is not your pet.");
                EphemeralData.getInstance().setPlayerAsNotSelecting(event.getPlayer());
                return;
            }

            Pet pet = PersistentData.getInstance().getPet(clickedEntity);

            EphemeralData.getInstance().selectPetForPlayer(pet, event.getPlayer());
            event.getPlayer().sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
        }

    }

}