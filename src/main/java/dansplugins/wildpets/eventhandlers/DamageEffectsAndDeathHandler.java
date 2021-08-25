package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class DamageEffectsAndDeathHandler implements Listener {

    private boolean debug = WildPets.getInstance().isDebugEnabled();

    @EventHandler()
    public void handle(EntityDamageByEntityEvent event) {
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling EntityDamageByEntity event to protect " + pet.getName() + "."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(AreaEffectCloudApplyEvent event) {
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling AreaEffectCloudApply event to protect " + pet.getName() + "."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(LingeringPotionSplashEvent event) {
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling LingeringPotionSplash event to protect " + pet.getName() + "."); }
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
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling PotionSplashEvent event to protect " + pet.getName() + "."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(EntityDamageByBlockEvent event) {
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            if (debug) { System.out.println("[DEBUG] Cancelling EntityDamageByBlockEvent event to protect " + pet.getName() + "."); }
            event.setCancelled(true);
        }
    }

}
