package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.services.ConfigService;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Daniel McCoy Stephenson
 */
public class DamageEffectsAndDeathHandler implements Listener {
    private final ConfigService configService;
    private final PersistentData persistentData;
    private final WildPets wildPets;

    public DamageEffectsAndDeathHandler(ConfigService configService, PersistentData persistentData, WildPets wildPets) {
        this.configService = configService;
        this.persistentData = persistentData;
        this.wildPets = wildPets;
    }

    @EventHandler()
    public void handle(EntityDamageEvent event) {
        if (configService.getBoolean("damageToPetsEnabled")) {
            return;
        }
        Pet pet = persistentData.getPet(event.getEntity());
        if (pet != null) {
            if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect " + pet.getName() + "."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(EntityDeathEvent event) {
        Pet pet = persistentData.getPet(event.getEntity());
        if (pet != null) {
            Player owner = Bukkit.getPlayer(pet.getOwnerUUID());
            String name = pet.getName();
            boolean success = persistentData.removePet(pet);
            if (success) {
                if (owner != null) {
                    owner.sendMessage(ChatColor.RED + name + " has died.");
                }
            }
            else {
                if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Something went wrong removing " + name + " from the persistent data."); }
            }
        }
    }
}