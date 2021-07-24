package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class InteractionHandler implements Listener {

    private boolean debug = true;

    @EventHandler()
    public void handle(PlayerInteractEntityEvent event) {
        if (debug) { System.out.println("Captured PlayerInteractEntity event!"); }

        Entity clickedEntity = event.getRightClicked();

        Player player = event.getPlayer();
        
        Pet pet = PersistentData.getInstance().getPet(clickedEntity);

        if (EphemeralData.getInstance().isPlayerTaming(player)) {
            setRightClickCooldown(player, 1);

            if (clickedEntity instanceof Player) {
                player.sendMessage(ChatColor.RED + "You can't tame players.");
                return;
            }

            if (pet != null) {
                player.sendMessage(ChatColor.RED + "That entity is already a pet.");
                EphemeralData.getInstance().setPlayerAsNotTaming(player);
                return;
            }

            PersistentData.getInstance().addNewPet(player, clickedEntity);
            player.sendMessage(ChatColor.GREEN + "Tamed.");
            EphemeralData.getInstance().setPlayerAsNotTaming(player);
        }
        else if (EphemeralData.getInstance().isPlayerSelecting(player)) {
            setRightClickCooldown(player, 1);

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "That entity is not a pet.");
                EphemeralData.getInstance().setPlayerAsNotSelecting(player);
                return;
            }

            if (PersistentData.getInstance().getPlayersPet(player, clickedEntity) == null) {
                player.sendMessage(ChatColor.RED + "That entity is not your pet.");
                EphemeralData.getInstance().setPlayerAsNotSelecting(player);
                return;
            }

            EphemeralData.getInstance().selectPetForPlayer(pet, player);
            player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
            EphemeralData.getInstance().setPlayerAsNotSelecting(player);
        }
        else {
            if (pet == null) {
                return;
            }

            if (!EphemeralData.getInstance().hasRightClickCooldown(player)) {
                setRightClickCooldown(player, 5);

                pet.sendInfoToPlayer(player);
            }
        }

    }

    private void setRightClickCooldown(Player player, int seconds) {
        EphemeralData.getInstance().setRightClickCooldown(player, true);

        WildPets.getInstance().getServer().getScheduler().runTaskLater(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                EphemeralData.getInstance().setRightClickCooldown(player, false);

            }
        }, seconds * 20);
    }

}