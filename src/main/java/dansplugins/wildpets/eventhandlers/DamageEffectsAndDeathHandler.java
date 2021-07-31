package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class DamageEffectsAndDeathHandler implements Listener {

    private boolean debug = true;

    @EventHandler()
    public void handle(EntityDamageByEntityEvent event) {
        if (PersistentData.getInstance().getPet(event.getEntity()) != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling EntityDamageByEntity event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(AreaEffectCloudApplyEvent event) {
        if (PersistentData.getInstance().getPet(event.getEntity()) != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling AreaEffectCloudApply event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(LingeringPotionSplashEvent event) {
        if (PersistentData.getInstance().getPet(event.getEntity()) != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling LingeringPotionSplash event."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(EntityDeathEvent event) {
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            Player owner = Bukkit.getPlayer(pet.getOwnerUUID());
            String name = pet.getName();
            boolean success = PersistentData.getInstance().removePet(pet);
            if (success) {
                if (owner != null) {
                    owner.sendMessage(ChatColor.RED + name + " has died.");
                }
            }
            else {
                if (debug) { System.out.println("[DEBUG] Something went wrong removing " + name + " from the persistent data."); }
            }
        }
    }

    @EventHandler()
    public void handle(PotionSplashEvent event) {
        if (PersistentData.getInstance().getPet(event.getEntity()) != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling PotionSplashEvent event."); }
            event.setCancelled(true);
        }
    }

}
