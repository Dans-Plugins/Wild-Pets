package dansplugins.wildpets.listeners;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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

    /**
     * Prevents direct damage to and from pets
     */
    @EventHandler()
    public void handle(EntityDamageEvent event) {
        if (!configService.getBoolean("damageToPetsEnabled")) {
            Pet pet = petListRepository.getPet(event.getEntity());
            if (pet != null) {
                if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect " + pet.getName() + "."); }
                event.setCancelled(true);
            }
        }
        if (!configService.getBoolean("damageFromPetsEnabled")) {
            // cancel direct damage from pets
            Pet damager = petListRepository.getPet(event.getEntity());
            if (damager != null) {
                if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect an entity from " + damager.getName() + "."); }
                event.setCancelled(true);
            }
        }

    }

    /**
     * Removes the pet from the persistent data when it dies
     */
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

    /**
     * Prevents direct damage to and from pets
     */
    @EventHandler()
    public void handle(EntityDamageByEntityEvent event) {
        if (!configService.getBoolean("damageToPetsEnabled")) {
            // if entity is a pet, cancel
            Pet pet = petListRepository.getPet(event.getEntity());
            if (pet != null) {
                if (wildPets.isDebugEnabled()) {
                    System.out.println("[DEBUG] Cancelling EntityDamageByEntityEvent event to protect " + pet.getName() + ".");
                }
                event.setCancelled(true);
            }
        }
        if (!configService.getBoolean("damageFromPetsEnabled")) {
            // if damager is a pet, cancel
            Pet damager = petListRepository.getPet(event.getDamager());
            if (damager != null) {
                if (wildPets.isDebugEnabled()) {
                    System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect an entity from " + damager.getName() + ".");
                }
                event.setCancelled(true);
            }

            // if damager is a projectile from a pet, cancel
            if (event.getDamager() instanceof org.bukkit.entity.Projectile || event.getDamager() instanceof org.bukkit.entity.ThrownPotion) {
                org.bukkit.entity.Projectile projectile = (org.bukkit.entity.Projectile) event.getDamager();
                try {
                    Entity entity = (Entity) projectile.getShooter();
                    Pet pet = petListRepository.getPet(entity);
                    if (pet != null) {
                        if (wildPets.isDebugEnabled()) {
                            System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect an entity from a projectile from " + pet.getName() + ".");
                        }
                        event.setCancelled(true);
                    }
                }
                catch (Exception e) {
                    // do nothing, shooter was probably not an entity
                }
            }

            // if damager is an area of effect potion
            if (event.getDamager() instanceof org.bukkit.entity.AreaEffectCloud) {
                org.bukkit.entity.AreaEffectCloud cloud = (org.bukkit.entity.AreaEffectCloud) event.getDamager();
                try {
                    Entity entity = (Entity) cloud.getSource();
                    Pet pet = petListRepository.getPet(entity);
                    if (pet != null) {
                        if (wildPets.isDebugEnabled()) {
                            System.out.println("[DEBUG] Cancelling EntityDamageEvent event to protect an entity from an area of effect potion from " + pet.getName() + ".");
                        }
                        event.setCancelled(true);
                    }
                }
                catch (Exception e) {
                    // do nothing, source was probably not an entity
                }
            }
        }
    }
}