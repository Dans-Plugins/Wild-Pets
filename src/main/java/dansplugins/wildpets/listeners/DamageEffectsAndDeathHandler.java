package dansplugins.wildpets.listeners;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.config.ConfigService;

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
    private final PetListRepository petListRepository;
    private final WildPets wildPets;

    public DamageEffectsAndDeathHandler(ConfigService configService, PetListRepository petListRepository, WildPets wildPets) {
        this.configService = configService;
        this.petListRepository = petListRepository;
        this.wildPets = wildPets;
    }

    @EventHandler()
    public void handle(EntityDamageEvent event) {
        if (configService.getBoolean("damageToPetsEnabled")) {
            return;
        }
        Pet pet = petListRepository.getPet(event.getEntity());
        if (pet != null) {
            if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect " + pet.getName() + "."); }
            event.setCancelled(true);
        }
    }

    @EventHandler()
    public void handle(EntityDeathEvent event) {
        Pet pet = petListRepository.getPet(event.getEntity());
        if (pet != null) {
            Player owner = Bukkit.getPlayer(pet.getOwnerUUID());
            String name = pet.getName();
            boolean success = petListRepository.removePet(pet);
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