package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

/**
 * @author Daniel McCoy Stephenson
 */
public class DamageEffectsAndDeathHandler implements Listener {

    @EventHandler()
    public void handle(EntityDamageEvent event) {
        if (ConfigManager.getInstance().getBoolean("damageToPetsEnabled")) {
            return;
        }
        Pet pet = PersistentData.getInstance().getPet(event.getEntity());
        if (pet != null) {
            if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect " + pet.getName() + "."); }
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
                if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Something went wrong removing " + name + " from the persistent data."); }
            }
        }
    }
}