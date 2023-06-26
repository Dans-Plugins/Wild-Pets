package dansplugins.wildpets.listeners;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.config.EntityConfigService;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import dansplugins.wildpets.scheduler.Scheduler;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class InteractionHandler implements Listener {
    private final EntityConfigService entityConfigService;
    private final PetListRepository petListRepository;
    private final PetRecordRepository petRecordRepository;
    private final EphemeralData ephemeralData;
    private final WildPets wildPets;
    private final ConfigService configService;
    private final Scheduler scheduler;

    public InteractionHandler(EntityConfigService entityConfigService, PetListRepository petListRepository, PetRecordRepository petRecordRepository, EphemeralData ephemeralData, WildPets wildPets, ConfigService configService, Scheduler scheduler) {
        this.entityConfigService = entityConfigService;
        this.petListRepository = petListRepository;
        this.petRecordRepository = petRecordRepository;
        this.ephemeralData = ephemeralData;
        this.wildPets = wildPets;
        this.configService = configService;
        this.scheduler = scheduler;
    }

    @EventHandler()
    public void handle(PlayerInteractEntityEvent event) {
        Entity clickedEntity = event.getRightClicked();
        
        Player player = event.getPlayer();
        
        Pet pet = petListRepository.getPet(clickedEntity);

        if (ephemeralData.isPlayerTaming(player.getUniqueId())) {
            setRightClickCooldown(player, 1);

            if (clickedEntity instanceof Player) {
                player.sendMessage(ChatColor.RED + "You can't tame players.");
                return;
            }

            if (!(clickedEntity instanceof LivingEntity)) {
                player.sendMessage(ChatColor.RED + "You can only tame living entities.");
                ephemeralData.setPlayerAsNotTaming(player.getUniqueId());
                return;
            }

            if (pet != null) {
                player.sendMessage(ChatColor.RED + "That entity is already a pet.");
                ephemeralData.setPlayerAsNotTaming(player.getUniqueId());
                return;
            }

            if (wildPets.isDebugEnabled() && entityConfigService.acquireConfiguration(clickedEntity).getType().equalsIgnoreCase("default")) {
                player.sendMessage(ChatColor.BLUE + "[DEBUG] This entity doesn't have a configuration.");
            }

            if (!entityConfigService.acquireConfiguration(clickedEntity).isEnabled()) {
                player.sendMessage(ChatColor.RED + "Taming has been disabled for this entity.");
                return;
            }

            int numPets = petListRepository.getPetList(player.getUniqueId()).getNumPets();
            int petLimit = configService.getInt("petLimit");
            if (wildPets.isDebugEnabled()) {
                System.out.println("[DEBUG] Number of pets: " + numPets);
                System.out.println("[DEBUG] Pet Limit: " + petLimit);
            }
            if (numPets >= petLimit) {
                player.sendMessage(ChatColor.RED + "You have reached your pet limit.");
                ephemeralData.setPlayerAsNotTaming(player.getUniqueId());
                return;
            }

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Material requiredMaterial = entityConfigService.acquireConfiguration(clickedEntity).getRequiredTamingItem();
            int requiredAmount = entityConfigService.acquireConfiguration(clickedEntity).getTamingItemAmount();
            if (itemStack.getType() != requiredMaterial || itemStack.getAmount() < requiredAmount) {
                player.sendMessage(ChatColor.RED + "You need to use " + requiredAmount + " " + requiredMaterial.name().toLowerCase() + " to tame this entity.");
                ephemeralData.setPlayerAsNotTaming(player.getUniqueId());
                return;
            }

            // handle chance to tame
            if (!rollDice(entityConfigService.acquireConfiguration(clickedEntity).getChanceToSucceed())) {
                player.sendMessage(ChatColor.RED + "Taming failed.");
                if (configService.getBoolean("cancelTamingAfterFailedAttempt")) {
                    ephemeralData.setPlayerAsNotTaming(player.getUniqueId());
                }
                if (itemStack.getAmount() > requiredAmount) {
                    player.getInventory().setItemInMainHand(new ItemStack(itemStack.getType(), itemStack.getAmount() - requiredAmount));
                }
                else {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
                return;
            }

            petListRepository.addNewPet(player, clickedEntity);
            player.sendMessage(ChatColor.GREEN + "Tamed.");
            ephemeralData.setPlayerAsNotTaming(player.getUniqueId());

            if (itemStack.getAmount() > requiredAmount) {
                player.getInventory().setItemInMainHand(new ItemStack(itemStack.getType(), itemStack.getAmount() - requiredAmount));
            }
            else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            ephemeralData.selectPetForPlayer(petListRepository.getPet(clickedEntity), player.getUniqueId());
        }
        else if (ephemeralData.isPlayerSelecting(player.getUniqueId())) {
            setRightClickCooldown(player, 1);

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "That entity is not a pet.");
                ephemeralData.setPlayerAsNotSelecting(player.getUniqueId());
                return;
            }

            if (petListRepository.getPlayersPet(player, clickedEntity) == null) {
                player.sendMessage(ChatColor.RED + "That entity is not your pet.");
                ephemeralData.setPlayerAsNotSelecting(player.getUniqueId());
                return;
            }

            ephemeralData.selectPetForPlayer(pet, player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
            ephemeralData.setPlayerAsNotSelecting(player.getUniqueId());
        }
        else if (ephemeralData.isPlayerLocking(player.getUniqueId())) {

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "This entity isn't a pet.");
                ephemeralData.setPlayerAsNotLocking(player.getUniqueId());
                return;
            }

            if (!pet.getOwnerUUID().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "This is not your pet.");
                ephemeralData.setPlayerAsNotLocking(player.getUniqueId());
                return;
            }

            boolean locked = pet.isLocked();
            if (locked) {
                player.sendMessage(ChatColor.RED + "This pet is already locked.");
                ephemeralData.setPlayerAsNotLocking(player.getUniqueId());
                return;
            }
            pet.setLocked(true);
            player.sendMessage(ChatColor.GREEN + "This pet has been locked.");
            ephemeralData.setPlayerAsNotLocking(player.getUniqueId());
            event.setCancelled(true);
        }
        else if (ephemeralData.isPlayerUnlocking(player.getUniqueId())) {

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "This entity isn't a pet.");
                ephemeralData.setPlayerAsNotUnlocking(player.getUniqueId());
                return;
            }

            if (!pet.getOwnerUUID().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "This is not your pet.");
                ephemeralData.setPlayerAsNotUnlocking(player.getUniqueId());
                return;
            }

            boolean locked = pet.isLocked();
            if (!locked) {
                player.sendMessage(ChatColor.RED + "This pet is already unlocked.");
                ephemeralData.setPlayerAsNotUnlocking(player.getUniqueId());
                return;
            }
            pet.setLocked(false);
            player.sendMessage(ChatColor.GREEN + "This pet has been unlocked.");
            ephemeralData.setPlayerAsNotUnlocking(player.getUniqueId());
            event.setCancelled(true);
        }
        else if (ephemeralData.isPlayerCheckingAccess(player.getUniqueId())) {

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "This entity isn't a pet.");
                ephemeralData.setPlayerAsNotCheckingAccess(player.getUniqueId());
                ephemeralData.setPlayerAsNotCheckingAccess(player.getUniqueId());
                return;
            }

            boolean locked = pet.isLocked();
            if (!locked) {
                player.sendMessage(ChatColor.RED + "This pet isn't locked.");
                ephemeralData.setPlayerAsNotCheckingAccess(player.getUniqueId());
                return;
            }

            if (pet.getAccessList().size() == 0) {
                player.sendMessage(ChatColor.RED + "No one has access to this pet.");
                ephemeralData.setPlayerAsNotCheckingAccess(player.getUniqueId());
                return;
            }

            if (pet.getAccessList().size() == 1 && pet.getAccessList().get(0).equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "No one has access to this pet but you.");
                ephemeralData.setPlayerAsNotCheckingAccess(player.getUniqueId());
                return;
            }

            player.sendMessage(ChatColor.AQUA + "The following players have access to this pet:");

            UUIDChecker uuidChecker = new UUIDChecker();

            for (UUID uuid : pet.getAccessList()) {
                String playerName = uuidChecker.findPlayerNameBasedOnUUID(uuid);
                if (playerName != null) {
                    player.sendMessage(ChatColor.AQUA + playerName);
                }
            }
            ephemeralData.setPlayerAsNotCheckingAccess(player.getUniqueId());
            event.setCancelled(true);
        }
        else if (ephemeralData.isPlayerGrantingAccess(player.getUniqueId())) {
            // TODO: implement
        }
        else if (ephemeralData.isPlayerRevokingAccess(player.getUniqueId())) {
            // TODO: implement
        }
        else {

            if (!ephemeralData.hasRightClickCooldown(player.getUniqueId())) {
                if (pet == null) {
                    return;
                }

                setRightClickCooldown(player, configService.getInt("rightClickViewCooldown"));

                pet.sendInfoToPlayer(player, configService, petRecordRepository);

                if (configService.getBoolean("rightClickToSelect")) {

                    if (!pet.getOwnerUUID().equals(player.getUniqueId())) {
                        return;
                    }

                    Pet petSelection = ephemeralData.getPetSelectionForPlayer(player.getUniqueId());
                    if (petSelection == null || !petSelection.getUniqueID().equals(pet.getUniqueID())) {
                        ephemeralData.selectPetForPlayer(pet, player.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
                    }

                }
            }

        }

        if (pet == null) {
            return;
        }

        if (pet.isLocked() && !pet.getOwnerUUID().equals(player.getUniqueId()) && !pet.getAccessList().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have access to this pet.");
            event.setCancelled(true);
        }

    }

    @EventHandler()
    public void handle(EntityMountEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;

        Entity mount = event.getMount();
        Pet pet = petListRepository.getPet(mount);
        if (pet == null) {
            return;
        }

        if (!pet.isLocked()) {
            return;
        }

        if (!pet.getOwnerUUID().equals(player.getUniqueId()) && !pet.getAccessList().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You don't have access to this pet.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "You mount " + pet.getName());
    }

    private void setRightClickCooldown(Player player, int seconds) {
        ephemeralData.setRightClickCooldown(player.getUniqueId(), true);
        scheduler.scheduleRightClickCooldownSetter(player, seconds);
    }

    private boolean rollDice(double chanceToSucceed) {
        double chanceToFail = 1 - chanceToSucceed;
        if (wildPets.isDebugEnabled()) { System.out.println("Rolling dice! Chance to fail: " + chanceToFail * 100 + "%"); }
        Random random = new Random();
        double generatedNumber = random.nextDouble();
        if (wildPets.isDebugEnabled()) { System.out.println("Dice landed on " + generatedNumber * 100 + ". " + chanceToFail * 100 + " was required."); }
        return generatedNumber > chanceToFail;
    }
}