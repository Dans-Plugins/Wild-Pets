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

        if (!EphemeralData.getInstance().isPlayerTaming(event.getPlayer())) {
            return;
        }

        Entity clickedEntity = event.getRightClicked();

        if (PersistentData.getInstance().isPet(clickedEntity)) {
            event.getPlayer().sendMessage(ChatColor.RED + "That entity is already a pet!");
            return;
        }

        PersistentData.getInstance().addNewPet(event.getPlayer(), clickedEntity);
        event.getPlayer().sendMessage(ChatColor.GREEN + "Tamed!");
        EphemeralData.getInstance().setPlayerAsNotTaming(event.getPlayer());
    }

}